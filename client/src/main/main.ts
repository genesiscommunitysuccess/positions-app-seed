import { Connect } from '@genesislcap/foundation-comms';
import { EventEmitter } from '@genesislcap/foundation-events';
import { Navigation } from '@genesislcap/foundation-header';
import { FASTElement, customElement, observable } from '@microsoft/fast-element';
import { Container, inject, Registration } from '@microsoft/fast-foundation';
import { DefaultRouteRecognizer } from '@microsoft/fast-router';
import { DynamicTemplate as template, LoadingTemplate, MainTemplate } from './main.template';
import { MainStyles as styles } from './main.styles';
import { MainRouterConfig } from '../routes';
import * as Components from '../components';
import {
  SwatchRGB,
  PaletteRGB,
  fillColor,
  neutralPalette,
  accentPalette,
  controlCornerRadius,
  designUnit,
} from '@microsoft/fast-components';
import { parseColorHexRGB, ColorRGBA64 } from '@microsoft/fast-colors';
import { HostENV, HostURL } from '../types';
import { Store, StoreEventDetailMap } from '../store';
import { logger } from '../utils';

const name = '{{rootElement}}';

export type EventMap = StoreEventDetailMap;

// eslint-disable-next-line
declare var API_HOST: string;

const hostEnv = location.host;
const hostUrl = API_HOST || `wss://${hostEnv}/gwf/`;

@customElement({
  name,
  template,
  styles,
})
export class MainApplication extends EventEmitter<EventMap>(FASTElement) {
  @inject(MainRouterConfig) config!: MainRouterConfig;
  @inject(Navigation) navigation!: Navigation;
  @Connect connect!: Connect;
  @Container container!: Container;
  @Store store: Store;
  @observable rootProvider!: any;
  @observable provider!: any;
  @observable ready: boolean = false;
  @observable data: any = null;

  public async connectedCallback() {
    super.connectedCallback();

    logger.debug(`${name} is now connected to the DOM`);

    this.registerDIDependencies();
    await this.loadRemotes();

    // TODO: double-check correct approach when using mf:login
    // this.handleConnection();

    const neutralPaletteColor: ColorRGBA64 = parseColorHexRGB('#19232e');
    neutralPalette.setValueFor(
      this.provider,
      PaletteRGB.from(
        SwatchRGB.create(neutralPaletteColor.r, neutralPaletteColor.g, neutralPaletteColor.b)
      )
    );
    const accentPaletteColor: ColorRGBA64 = parseColorHexRGB('#247592');
    accentPalette.setValueFor(
      this.provider,
      PaletteRGB.from(
        SwatchRGB.create(accentPaletteColor.r, accentPaletteColor.g, accentPaletteColor.b)
      )
    );

    fillColor.setValueFor(this.provider, SwatchRGB.from(parseColorHexRGB('#181a1f')));

    controlCornerRadius.setValueFor(this.provider, 4);

    designUnit.setValueFor(this.provider, 3);

    this.$emit('store-connected', this.rootProvider);
    this.$emit('store-ready', true);
  }

  public disconnectedCallback() {
    super.disconnectedCallback();
    this.handleDisconnection();
  }

  public async loadRemotes() {
    /**
     * TODO: Send event to indicate some async work is happening.
     * Will be picked up by overlay micro frontend.
     */
    const remoteComponents = await Components.loadRemotes();
    /**
     * Simulate loading delay
     * await new Promise(resolve => setTimeout(resolve, 3000));
     */
    this.ready = true;
  }

  /**
   * You can use various directives in templates like when(), which enables conditional rendering,
   * and you can also split your templates up and return them based on some state.
   */
  public selectTemplate() {
    return this.ready ? MainTemplate : LoadingTemplate;
  }

  public handleDoingSomething(detail: any) {
    logger.debug(`handleDoingSomething in main ${detail}`);
  }

  private registerDIDependencies() {
    this.container.register(Registration.transient(DefaultRouteRecognizer, DefaultRouteRecognizer));
    this.container.register(Registration.instance(HostENV, hostEnv));
    this.container.register(Registration.instance(HostURL, hostUrl));
  }

  /**
   * With @attr or @observable properties defined, you can optionally implement a propertyNameChanged method
   * to respond to changes in your own state.
   */
  public providerChanged() {
    /**
     * Configure this design system provider if needed
     */
  }

  public async handleConnection() {
    if (this.connect.isConnected) {
      return;
    }

    try {
      this.connect.connect(hostUrl);
    } catch (err) {
      logger.debug(err);
    }
  }

  public async handleDisconnection() {
    if (!this.connect.isConnected) {
      return;
    }

    this.connect.disconnect();
    logger.debug(`Disconnected from ${hostUrl}`);
  }
}

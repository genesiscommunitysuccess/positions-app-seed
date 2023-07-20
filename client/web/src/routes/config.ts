import { Constructable } from '@microsoft/fast-element';
import { Container } from '@microsoft/fast-foundation';
import { Route, RouterConfiguration } from '@microsoft/fast-router';
import {
  Auth,
  FoundationAnalytics,
  FoundationAnalyticsEvent,
  FoundationAnalyticsEventType,
  Session,
} from '@genesislcap/foundation-comms';
import { defaultLayout, loginLayout } from './.layouts';
import { Users } from '@genesislcap/foundation-entity-management';
import { Home } from './home/home';
import { Analytics } from './analytics/analytics';
import { NotFound } from './not-found/not-found';
import { Extras } from './extras/extras';
import { Reporting } from '@genesislcap/foundation-reporting';
import { Alerts } from './alerts/alerts';

type RouteSettings = {
  public?: boolean;
  foo?: string;
};

export class MainRouterConfig extends RouterConfiguration<any> {
  constructor(
    @Auth private auth: Auth,
    @Container private container: Container,
    @FoundationAnalytics private analytics: FoundationAnalytics,
    @Session private session: Session
  ) {
    super();
  }

  public allRoutes = [
    { index: 1, path: 'protected', title: 'Home', icon: 'home', variant: 'solid' },
    { index: 2, path: 'alerts', title: 'Alerts', icon: 'cog', variant: 'solid' },
    // { index: 3, path: 'analytics', title: 'Analytics', icon: 'cog', variant: 'solid' },
    // { index: 4, path: 'extras', title: 'Extras', icon: 'cog', variant: 'solid' },
    // { index: 6, path: 'admin', title: 'Admin', icon: 'cog', variant: 'solid' },
  ];

  public configure() {
    this.title = 'Positions';
    this.defaultLayout = defaultLayout;
    this.routes.map(
      { path: '', redirect: 'protected' },
      {
        path: 'login',
        element: async () => {
          const { Login, configure } = await import('@genesislcap/foundation-login');
          configure(this.container, {
            // // Below properties are required for SSO
            // autoAuth: true,
            // autoConnect: true,
            // sso: {
            //   toggled: true,
            //   identityProvidersPath: '/gwf/sso/list',
            // },
            hostPath: 'login',
            defaultRedirectUrl: 'protected',
          });
          return Login;
        },
        title: 'Login',
        name: 'login',
        settings: {
          public: true,
        },
        childRouters: true,
        layout: loginLayout,
      },
      { path: 'admin', element: Users, title: 'Admin', name: 'admin' },
      { path: 'protected', element: Home, title: 'Home', name: 'protected' },
      { path: 'alerts', element: Alerts, title: 'Alerts', name: 'alerts' },
      { path: 'analytics', element: Analytics, title: 'Analytics', name: 'analytics' },
      { path: 'not-found', element: NotFound, title: 'Not Found', name: 'not-found' },
      { path: 'extras', element: Extras, title: 'Extras', name: 'extras' },
      { path: 'reporting', element: Reporting, title: 'Reporting', name: 'reporting' }
    );

    /**
     * Example of a FallbackRouteDefinition
     */
    this.routes.fallback(() =>
      this.auth.isLoggedIn ? { redirect: 'not-found' } : { redirect: 'login' }
    );

    /**
     * Example of a NavigationContributor
     */
    this.contributors.push({
      navigate: (phase) => {
        const settings = phase.route.settings;
        /**
         * TODO: Centralise
         * Suspect this should be done via createEventSink, but it's not fully clear how-to do that as no docs
         */
        this.analytics.trackEvent(FoundationAnalyticsEventType.routeChanged, <
          FoundationAnalyticsEvent.RouteChanged
        >{
          path: phase.route.endpoint.path,
        });

        /**
         * If public route don't block
         */
        if (settings && settings.public) {
          return;
        }

        /**
         * If logged in don't block
         */
        if (this.auth.isLoggedIn) {
          return;
        }

        /**
         * Otherwise route them somewhere, like to a login
         */
        phase.cancel(() => {
          this.session.captureReturnUrl();
          Route.name.replace(phase.router, 'login');
        });
      },
    });
  }

  public construct<T>(Type: Constructable<T>): T {
    return this.container.get(Type) as T;
  }
}

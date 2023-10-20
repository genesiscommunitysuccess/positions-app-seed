import { customElement, FASTElement } from '@microsoft/fast-element';
import { ExtrasTemplate as template } from './extras.template';
import { ExtrasStyles as styles } from './extras.styles';

@customElement({
  name: 'extras-route',
  template,
  styles,
})
export class Extras extends FASTElement {}

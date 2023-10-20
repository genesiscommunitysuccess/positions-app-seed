import {
    CriteriaBuilder,
    ExpressionBuilder,
    Expression,
    Serialiser,
  } from '@genesislcap/foundation-criteria';

  export const criteriaBuilder = (): CriteriaBuilder => new CriteriaBuilder();

  export const expressionBuilder = (
    field: string,
    value: unknown,
    serialiser: Serialiser
  ): Expression => {
    return new ExpressionBuilder()
      .withField(field)
      .withValue(value)
      .withSerialiser(serialiser)
      .build();
  };

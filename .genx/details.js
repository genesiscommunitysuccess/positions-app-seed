const { description, license, name, version } = require("./package.json");

const summary = `
  Name: ${name}
  Description: ${description}
  Version: ${version}
  License: ${license}
`;

const nextStepsMessage = ``;

module.exports = {
  description,
  license,
  name,
  summary,
  version,
  nextStepsMessage,
};

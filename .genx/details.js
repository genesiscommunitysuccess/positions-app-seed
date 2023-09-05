const {description, license, name, version} = require('./package.json');

const summary = `
  Name: ${name}
  Description: ${description}
  Version: ${version}
  License: ${license}
`;

const nextStepsMessage = `
 > Install dependencies with \`npm run bootstrap\`\n
 > Start development server with \`npm run dev\`
`;

const exclude = [
  './data/a.txt', // file
  './data2',      // dir
  './data3',      // invalid
]

module.exports = {
  description,
  license,
  name,
  summary,
  version,
  nextStepsMessage,
  exclude,
}

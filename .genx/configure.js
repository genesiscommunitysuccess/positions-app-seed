const path = require('path');
const answers = require('./answers');

/**
 * Signature is `async (data: inquirer.Answers, utils: SeedConfigurationUtils)`
 *
 * See here for up-to-date interface:
 * https://github.com/genesislcap/foundation-ui/tree/master/packages/tooling/foundation-cli/src/utils
 *
 * Example:
 *
 * interface SeedConfigurationUtils {
 *    plop: NodePlopAPI;
 *    progressSpinner: Ora;
 *    editJSONFile(path: string, options: any);
 *    readFile(file: PathOrFileDescriptor): string;
 *    writeFile(file: PathOrFileDescriptor, data: string): void;
 *    writeFileWithData(file: PathOrFileDescriptor, answers: inquirer.Answers): void;
 *    copyFiles(source: string, destination: string, options: CopyOptionsSync): void;
 *    copyFilesAsync(source: string, destination: string, options: CopyOptionsSync): Promise<void>;
 *    replaceObjectKeys(input: object, searchValue: string | RegExp, replaceValue: string): object;
 *    directoryExists(directory: PathLike): boolean;
 *    makeDirectory(directory: PathLike): void;
 * }
 */
module.exports = async (data, utils) => {
  const {directory, pkgScope, pkgName, apiHost} = data;
  const {editJSONFile, replaceObjectKeys, writeFileWithData} = utils;
  const prefixTarget = 'pkgScope/pkgName';
  const prefixReplacement = `${pkgScope}/${pkgName}-`;

  /**
   * Write answers data for future use by the CLI
   */
  answers.write(data);

  /**
   * Update the client root package.json file
   */
  const rootPackageFile = editJSONFile(`${directory}/client/package.json`);
  rootPackageFile.set('name', `@${prefixReplacement}root`);
  const scripts = rootPackageFile.get('scripts');
  scripts[`client:web`] = `npm run baseline && npx lerna run --scope @${prefixReplacement}web-client --parallel dev`;
  scripts[`copy-files:web`] = `npx lerna run --scope @${prefixReplacement}web-client copy-files`;
  scripts[`serve:web`] = `npx lerna run --scope @${prefixReplacement}web-client serve`;
  rootPackageFile.set('scripts', scripts);
  rootPackageFile.save();

  /**
   * Update the client root .npmrc file
   */
  writeFileWithData(path.resolve(directory, 'client/.npmrc'), data, path.resolve(directory, '.genx/plop/templates/.npmrc.hbs'));

  /**
   * Update the README.md files
   * TODO: Create handlebar templates for the README.md files in ./plop/templates due to re-configure re-runs
   */
  writeFileWithData(path.resolve(directory, 'README.md'), data);

  /**
   * Update the names and dependencies of the lerna managed packages
   */
  const updateLernaPackage = (file) => {
    const packageFile = editJSONFile(file);
    const packageName = packageFile.get('name');
    packageFile.set('name', packageName.replace(`${prefixTarget}-`, prefixReplacement));
    // packageFile.set('dependencies', replaceObjectKeys(packageFile.get('dependencies'), `${prefixTarget}-`, prefixReplacement));
    packageFile.save();
  };
  updateLernaPackage(`${directory}/client/web/package.json`);

  /**
   * Set apiHost in the web client
   */
  const webPackageFile = editJSONFile(`${directory}/client/web/package.json`);
  webPackageFile.set('config.API_HOST', apiHost);
  webPackageFile.save();

  /**
   * TODO: Create handlebar templates for README.md files in ./plop/templates due to re-configure re-runs
   * We should inspect the seed type in data / answers and use the correct README, for example a local seed won't need
   * the upstream instructions.
   */

  /**
   * TODO: Perhaps delete unrequested parts of this project, server variants, clients etc. Lift directories up.
   */
};

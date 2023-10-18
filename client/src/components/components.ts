import { allComponents, provideFASTDesignSystem } from '@microsoft/fast-components';
import { logger } from '../utils';
import { zeroGridComponents } from '@genesislcap/foundation-zero-grid-pro';
import { g2plotChartsComponents } from '@genesislcap/g2plot-chart';
import { FlexLayout, provideDesignSystem } from '@genesislcap/foundation-zero';
import { ErrorBoundary } from '@genesislcap/foundation-ui';
import { ModuleRegistry } from '@ag-grid-community/core';
import { LicenseManager } from '@ag-grid-enterprise/core';
import { ServerSideRowModelModule } from '@ag-grid-enterprise/server-side-row-model';
import { EntityManagement } from '@genesislcap/foundation-entity-management';
import { Filters, Form } from '@genesislcap/foundation-forms';
import { foundationLayoutComponents } from '@genesislcap/foundation-layout';
import { CriteriaSegmentedControl } from '@genesislcap/foundation-criteria';
import { FoundationInbox } from '@genesislcap/foundation-inbox';

const licenseKey =
  'CompanyName=GENESIS GLOBAL TECHNOLOGY LIMITED,LicensedGroup=Genesis Web,LicenseType=MultipleApplications,LicensedConcurrentDeveloperCount=3,LicensedProductionInstancesCount=4,AssetReference=AG-022858,ExpiryDate=8_January_2023_[v2]_MTY3MzEzNjAwMDAwMA==226d559d73e979c636d5f08eaa790ea8';

ModuleRegistry.registerModules([ServerSideRowModelModule]);
LicenseManager.setLicenseKey(licenseKey);
provideFASTDesignSystem().register(allComponents);
provideDesignSystem().register(
  zeroGridComponents,
  g2plotChartsComponents,
  foundationLayoutComponents,
);

FlexLayout;
ErrorBoundary;
EntityManagement;
Form;
Filters;
CriteriaSegmentedControl;
FoundationInbox;

enum ResourceType {
  local = 'local',
  remote = 'remote',
}

function loadZeroFallback() {
  return import(
    /* webpackMode: "lazy" */
    '@genesislcap/foundation-zero'
  );
}

export const loadZeroDesignSystem = async () => {
  let type = ResourceType.remote;
  try {
    // @ts-ignore
    return await import('foundationZero/ZeroDesignSystem');
  } catch (e) {
    type = ResourceType.local;
    return await loadZeroFallback();
  } finally {
    logger.debug(`Using '${type}' version of foundationZero/ZeroDesignSystem`);
  }
};

/**
 * Load the wp5 module federation remote versions, or fallback to code split bundled versions.
 * You would really be targeting the client's design system, components etc here. For now just targeting zero.
 */
export const loadRemotes = async () => {
  const { registerZeroDesignSystem } = await loadZeroDesignSystem();
  return {
    ZeroDesignSystem: registerZeroDesignSystem(),
  };
};

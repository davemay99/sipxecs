/*
 *
 *
 * Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 *
 * $
 */
package org.sipfoundry.sipxconfig.ftp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.sipfoundry.sipxconfig.address.Address;
import org.sipfoundry.sipxconfig.address.AddressManager;
import org.sipfoundry.sipxconfig.address.AddressProvider;
import org.sipfoundry.sipxconfig.address.AddressType;
import org.sipfoundry.sipxconfig.common.SipxHibernateDaoSupport;
import org.sipfoundry.sipxconfig.commserver.Location;
import org.sipfoundry.sipxconfig.commserver.LocationsManager;
import org.sipfoundry.sipxconfig.feature.Bundle;
import org.sipfoundry.sipxconfig.feature.BundleConstraint;
import org.sipfoundry.sipxconfig.feature.FeatureProvider;
import org.sipfoundry.sipxconfig.feature.GlobalFeature;
import org.sipfoundry.sipxconfig.feature.LocationFeature;
import org.sipfoundry.sipxconfig.firewall.DefaultFirewallRule;
import org.sipfoundry.sipxconfig.firewall.FirewallManager;
import org.sipfoundry.sipxconfig.firewall.FirewallProvider;
import org.sipfoundry.sipxconfig.firewall.FirewallRule;
import org.sipfoundry.sipxconfig.setting.BeanWithSettingsDao;
import org.sipfoundry.sipxconfig.setup.SetupListener;
import org.sipfoundry.sipxconfig.setup.SetupManager;
import org.sipfoundry.sipxconfig.snmp.ProcessDefinition;
import org.sipfoundry.sipxconfig.snmp.ProcessProvider;
import org.sipfoundry.sipxconfig.snmp.SnmpManager;

public class FtpManagerImpl extends SipxHibernateDaoSupport<Object> implements FtpManager, ProcessProvider,
    SetupListener, FeatureProvider, FirewallProvider, AddressProvider {
    private static final Collection<AddressType> ADDRESSES = Arrays.asList(TFTP_ADDRESS, FTP_ADDRESS, FTP_DATA_ADDRESS);
    private LocationsManager m_locationsManager;
    private BeanWithSettingsDao<FtpSettings> m_settingsDao;

    @Override
    public Collection<ProcessDefinition> getProcessDefinitions(SnmpManager manager, Location location) {
        return (location.isPrimary() ? Collections.singleton(new ProcessDefinition("vsftpd")) : null);
    }

    @Override
    public void setup(SetupManager manager) {
        if (!manager.isSetup(FTP_FEATURE.getId())) {
            Location primary = manager.getConfigManager().getLocationManager().getPrimaryLocation();
            manager.getFeatureManager().enableLocationFeature(FTP_FEATURE, primary, true);
            manager.setSetup(FTP_FEATURE.getId());
        }
    }

    @Override
    public Collection<GlobalFeature> getAvailableGlobalFeatures() {
        return null;
    }

    @Override
    public Collection<LocationFeature> getAvailableLocationFeatures(Location l) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getBundleFeatures(Bundle b) {
        if (b.isBasic()) {
            // Primary only because of local manipulation of files by sipxconfig
            b.addFeature(FTP_FEATURE, BundleConstraint.PRIMARY_ONLY);
        }
    }

    @Override
    public Collection<DefaultFirewallRule> getFirewallRules(FirewallManager manager) {
        return DefaultFirewallRule.rules(Arrays.asList(FTP_ADDRESS, FTP_DATA_ADDRESS, TFTP_ADDRESS),
                FirewallRule.SystemId.PUBLIC);
    }

    @Override
    public Collection<Address> getAvailableAddresses(AddressManager manager, AddressType type, Location requester) {
        if (!ADDRESSES.contains(type)) {
            return null;
        }

        // this assumes admin ui is also tftp and ftp server, which is a correct assumption
        // for now.
        Location primary = m_locationsManager.getPrimaryLocation();
        Address address = new Address(type, primary.getAddress());
        return Collections.singleton(address);
    }

    public void setLocationsManager(LocationsManager locationsManager) {
        m_locationsManager = locationsManager;
    }

    public void setSettingsDao(BeanWithSettingsDao<FtpSettings> settingsDao) {
        m_settingsDao = settingsDao;
    }

    @Override
    public FtpSettings getSettings() {
        return m_settingsDao.findOrCreateOne();
    }

    @Override
    public void saveSettings(FtpSettings settings) {
        m_settingsDao.upsert(settings);
    }
}
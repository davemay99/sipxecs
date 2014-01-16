package org.sipfoundry.sipxconfig.paging;

import java.util.Collection;

public interface PagingFeatureContext {

    public abstract void deletePagingGroupsById(Collection<Integer> groupsIds);

}

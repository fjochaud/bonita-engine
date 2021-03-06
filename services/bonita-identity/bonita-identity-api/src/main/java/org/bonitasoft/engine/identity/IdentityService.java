/**
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the ied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.identity;

import java.util.List;
import java.util.Set;

import org.bonitasoft.engine.identity.model.SContactInfo;
import org.bonitasoft.engine.identity.model.SGroup;
import org.bonitasoft.engine.identity.model.SProfileMetadataDefinition;
import org.bonitasoft.engine.identity.model.SProfileMetadataValue;
import org.bonitasoft.engine.identity.model.SRole;
import org.bonitasoft.engine.identity.model.SUser;
import org.bonitasoft.engine.identity.model.SUserMembership;
import org.bonitasoft.engine.persistence.OrderByOption;
import org.bonitasoft.engine.persistence.OrderByType;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.persistence.SBonitaSearchException;
import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;

/**
 * @author Anthony Birembaut
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 * @since 6.0
 */
public interface IdentityService {

    String GROUP = "GROUP";

    String METADATA = "METADATA";

    String METADATAVALUE = "METADATAVALUE";

    String ROLE = "ROLE";

    String USER = "USER";

    String USER_CONTACT_INFO = "USER_CONTACT_INFO";

    String USERMEMBERSHIP = "USERMEMBERSHIP";

    /**
     * Gets the role given by its identifier.
     * 
     * @param roleId
     *            the role identifier
     * @return the role
     * @throws SRoleNotFoundException
     *             occurs when the roleId does not refer to any role.
     */
    SRole getRole(long roleId) throws SRoleNotFoundException;

    /**
     * Get the role given by its name.
     * 
     * @param roleName
     *            The name of role
     * @return the role
     * @throws SRoleNotFoundException
     *             occurs when the roleName does not refer to any role.
     */
    SRole getRoleByName(String roleName) throws SRoleNotFoundException;

    /**
     * Get the group by its path
     * 
     * @param groupPath
     *            The group path
     * @return the group
     * @throws SGroupNotFoundException
     *             Occurs when the groupPath does not refer to any group.
     */
    SGroup getGroupByPath(String groupPath) throws SGroupNotFoundException;

    /**
     * Get total number of roles
     * 
     * @return the total number of roles
     * @throws SIdentityException
     */
    long getNumberOfRoles() throws SIdentityException;

    /**
     * Get roles by their ids
     * 
     * @param roleIds
     *            the role identifiers
     * @return a list of SRole objects
     * @throws SRoleNotFoundException
     *             Occurs when roleId does not refer to any role.
     */
    List<SRole> getRoles(List<Long> roleIds) throws SRoleNotFoundException;

    /**
     * Get roles in specific interval
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfRoles
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SRole objects
     * @throws SIdentityException
     */
    List<SRole> getRoles(int fromIndex, int numberOfRoles) throws SIdentityException;

    /**
     * Get roles in specific interval, The returned list is paginated
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfRoles
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of paginated SRole objects
     * @throws SIdentityException
     */
    List<SRole> getRoles(int fromIndex, int numberOfRoles, String field, OrderByType order) throws SIdentityException;

    /**
     * Get group by its id
     * 
     * @param groupId
     *            The group identifier
     * @return the group
     * @throws SGroupNotFoundException
     *             occurs when the groupId does not refer to any group.
     */
    SGroup getGroup(long groupId) throws SGroupNotFoundException;

    /**
     * Get total number of groups
     * 
     * @return the total number of groups
     * @throws SIdentityException
     */
    long getNumberOfGroups() throws SIdentityException;

    /**
     * Get all groups having the specific name
     * 
     * @param groupName
     *            The group name
     * @return a set of SGroup object
     * @throws SGroupNotFoundException
     *             occurs when the groupName does not refer to any group.
     */
    Set<SGroup> getGroupsByName(String groupName) throws SGroupNotFoundException;

    /**
     * Get groups for specific groupIds
     * 
     * @param groupIds
     *            The group identifiers
     * @return a list of SGroup object
     * @throws SGroupNotFoundException
     */
    List<SGroup> getGroups(List<Long> groupIds) throws SGroupNotFoundException;

    /**
     * Get groups in a specific interval, this is used for pagination
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfGroups
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SGroup objects
     * @throws SIdentityException
     */
    List<SGroup> getGroups(int fromIndex, int numberOfGroups) throws SIdentityException;

    /**
     * Get groups in specific interval, The returned list is paginated
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfGroups
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of paginated SGroup objects
     * @throws SIdentityException
     */
    List<SGroup> getGroups(int fromIndex, int numberOfGroups, String field, OrderByType order) throws SIdentityException;

    /**
     * Get number of child groups for the specific group
     * 
     * @param parentGroupId
     *            The parent group identifier
     * @return the number of child groups
     * @throws SIdentityException
     */
    long getNumberOfGroupChildren(long parentGroupId) throws SIdentityException;

    /**
     * Get all child groups for the specified group
     * 
     * @param parentGroupId
     *            The parent group identifier
     * @return a list of SGroup objects
     * @throws SIdentityException
     */
    List<SGroup> getGroupChildren(long parentGroupId) throws SIdentityException;

    /**
     * Get child groups in a specific interval for specific group, this is used for pagination
     * 
     * @param parentGroupId
     *            The parent group identifier
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfGroups
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SGroup objects
     * @throws SIdentityException
     */
    List<SGroup> getGroupChildren(long parentGroupId, int fromIndex, int numberOfGroups) throws SIdentityException;

    /**
     * Get child groups in specific interval, The returned list is paginated
     * 
     * @param parentGroupId
     *            The parent group identifier
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfGroups
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of SGroup objects
     * @throws SIdentityException
     */
    List<SGroup> getGroupChildren(long parentGroupId, int fromIndex, int numberOfGroups, String field, OrderByType order) throws SIdentityException;

    /**
     * Get user by its id
     * 
     * @param userId
     *            The user identifier
     * @return the user
     * @throws SUserNotFoundException
     *             occurs when the userId does not refer to any user.
     */
    SUser getUser(long userId) throws SUserNotFoundException;

    /**
     * Checks whether the couple user/password is valid.
     * 
     * @param user
     *            the user
     * @param password
     *            the password
     * @return true if the couple user/password is valid; false otherwise
     */
    boolean chechCredentials(SUser user, String password);

    /**
     * Get user by its name
     * 
     * @param username
     *            The user name
     * @return the user
     * @throws SUserNotFoundException
     *             occurs when the user name does not refer to any user.
     */
    SUser getUserByUserName(String username) throws SUserNotFoundException;

    /**
     * Get total number of users
     * 
     * @return the total number of users
     * @throws SIdentityException
     */
    long getNumberOfUsers() throws SIdentityException;

    /**
     * Get users by their userIds
     * 
     * @param userIds
     *            A list of user identifiers
     * @return a list of SUser objects
     * @throws SUserNotFoundException
     *             occurs when the userId does not refer to any user.
     */
    List<SUser> getUsers(List<Long> userIds) throws SUserNotFoundException;

    /**
     * Lists the users from their names.
     * 
     * @param userNames
     *            the list of user names
     * @return the list of users
     * @throws SIdentityException
     *             If an exception occurs when retrieving the users
     */
    List<SUser> getUsersByUsername(List<String> userNames) throws SIdentityException;

    /**
     * Get users in a specific interval, this is used for pagination
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsers(int fromIndex, int numberOfUsers) throws SIdentityException;

    /**
     * Get users in specific interval, The returned list is paginated
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of paginated SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsers(int fromIndex, int numberOfUsers, String field, OrderByType order) throws SIdentityException;

    /**
     * Get all users managed by a specific manager
     * 
     * @param managerId
     *            The manager identifier, actually it is user identifier
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByManager(long managerId) throws SIdentityException;

    /**
     * Get all users delegated by a specific delegate
     * 
     * @param delegateId
     *            The delegate identifier, actually it is user identifier
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByDelegee(long delegateId) throws SIdentityException;

    /**
     * Get total number of users for the given role
     * 
     * @param roleId
     *            The identifier of role
     * @return total number of users related to the given role
     * @throws SIdentityException
     */
    long getNumberOfUsersByRole(long roleId) throws SIdentityException;

    /**
     * Get total users for the given role
     * 
     * @param roleId
     *            The identifier of the role
     * @return a list of SUser object
     * @throws SIdentityException
     */
    List<SUser> getUsersByRole(long roleId) throws SIdentityException;

    /**
     * Get users in a specific interval for the given role, this is used for pagination
     * 
     * @param roleId
     *            The identifier of the role
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByRole(long roleId, int fromIndex, int numberOfUsers) throws SIdentityException;

    /**
     * Get users in specific interval for given role, The returned list is paginated
     * 
     * @param roleId
     *            The identifier of the role
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of paginated SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByRole(long roleId, int fromIndex, int numberOfUsers, String field, OrderByType order) throws SIdentityException;

    /**
     * Get total number of users for the given group
     * 
     * @param groupId
     *            The identifier of the group
     * @return total number of users in the given group
     * @throws SIdentityException
     */
    long getNumberOfUsersByGroup(long groupId) throws SIdentityException;

    /**
     * Get total users for the given group
     * 
     * @param groupId
     *            Identifier of the group
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByGroup(long groupId) throws SIdentityException;

    /**
     * Get users in a specific interval for the given group, this is used for pagination
     * 
     * @param groupId
     *            Identifier of the group
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByGroup(long groupId, int fromIndex, int numberOfUsers) throws SIdentityException;

    /**
     * Get users in specific interval for given group, The returned list is paginated
     * 
     * @param groupId
     *            Identifier of the group
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUsers
     *            Number of result we want to get. Maximum number of result returned.
     * @param field
     *            The field used by the order
     * @param order
     *            ASC or DESC
     * @return a list of SUser objects
     * @throws SIdentityException
     */
    List<SUser> getUsersByGroup(long groupId, int fromIndex, int numberOfUsers, String field, OrderByType order) throws SIdentityException;

    /**
     * Get user memberships for given group
     * 
     * @param groupId
     *            Identifier of the group
     * @param maxResults
     * @param startIndex
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfGroup(long groupId, int startIndex, int maxResults) throws SIdentityException;

    /**
     * Get user memberships for given role
     * 
     * @param roleId
     *            Identifier of the role
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfRole(long roleId, int startIndex, int maxResults) throws SIdentityException;

    /**
     * Get userMembership by given id
     * 
     * @param userMembershipId
     *            The identifier of userMembership
     * @return a SUserMembership object
     * @throws SIdentityException
     */
    SUserMembership getUserMembership(long userMembershipId) throws SIdentityException;

    /**
     * Get user membership of specific user, group and role
     * 
     * @param userId
     *            The identifier of user
     * @param groupId
     *            The identifier of group
     * @param roleId
     *            The identifier of role
     * @return a SUserMembership object
     * @throws SIdentityException
     */
    SUserMembership getUserMembership(long userId, long groupId, long roleId) throws SIdentityException;

    /**
     * Get light user membership of specific user, group and role
     * 
     * @param userId
     *            The identifier of user
     * @param groupId
     *            The identifier of group
     * @param roleId
     *            The identifier of role
     * @return a SUserMembership object without userName, groupName and roleName
     * @throws SIdentityException
     */
    SUserMembership getLightUserMembership(long userId, long groupId, long roleId) throws SIdentityException;

    /**
     * Get userMembership by given id
     * 
     * @param userMembershipIds
     *            The identifier of userMembership
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMemberships(List<Long> userMembershipIds) throws SIdentityException;

    /**
     * Get userMemberships in a specific interval, this is used for pagination
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUserMemberships
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMemberships(int fromIndex, int numberOfUserMemberships) throws SIdentityException;

    /**
     * Get userMemberships in a specific interval with specific order, this is used for pagination
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfUserMemberships
     *            Number of result we want to get. Maximum number of result returned
     * @param orderByOption
     *            OrderByOption object containing order by information
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMemberships(int fromIndex, int numberOfUserMemberships, OrderByOption orderByOption) throws SIdentityException;

    /**
     * Get profileMetadataDefinition by its id
     * 
     * @param profileMetadataDefinitionId
     *            The identifier of profileMetadataDefinition
     * @return the profileMetadataDefinition
     * @throws SIdentityException
     */
    SProfileMetadataDefinition getProfileMetadataDefinition(long profileMetadataDefinitionId) throws SIdentityException;

    /**
     * Get profileMetadataValue by its id
     * 
     * @param profileMetadataValueId
     *            The identifier of profileMetadataValue
     * @return the profileMetadataValue
     * @throws SIdentityException
     */
    SProfileMetadataValue getProfileMetadataValue(long profileMetadataValueId) throws SIdentityException;

    /**
     * Get profileMetadataValue by its name
     * 
     * @param metadataName
     *            The name of profileMetadataValue
     * @return the profileMetadataValue
     * @throws SIdentityException
     */
    SProfileMetadataDefinition getProfileMetadataByName(String metadataName) throws SIdentityException;

    /**
     * Get total number of profileMetadataDefinition
     * 
     * @return the total number of profileMetadataDefinition
     * @throws SIdentityException
     */
    long getNumberOfProfileMetadataDefinition() throws SIdentityException;

    /**
     * Get profileMetadataDefinitions by their ids
     * 
     * @param profileMetadataDefinitionIds
     *            A list of identifiers of profileMetadataDefinition
     * @return a list of SProfileMetadataDefinition objects corresponding to parameters
     * @throws SIdentityException
     */
    List<SProfileMetadataDefinition> getProfileMetadataDefinitions(List<Long> profileMetadataDefinitionIds) throws SIdentityException;

    /**
     * Get profileMetadataValues by their ids
     * 
     * @param profileMetadataValueIds
     *            A list of identifiers of profileMetadataValue
     * @return a list of SProfileMetadataValue objects corresponding to parameters
     * @throws SIdentityException
     */
    List<SProfileMetadataValue> getProfileMetadataValues(List<Long> profileMetadataValueIds) throws SIdentityException;

    /**
     * Get profileMetadataDefinition in a specific interval, this is used for pagination
     * 
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfMetadata
     *            Number of result we want to get. Maximum number of result returned
     * @return a list of SProfileMetadataDefinition object
     * @throws SIdentityException
     */
    List<SProfileMetadataDefinition> getProfileMetadataDefinition(int fromIndex, int numberOfMetadata) throws SIdentityException;

    /**
     * Get userMemberships in a specific interval for a user, this is used for pagination
     * 
     * @param userId
     *            The identifier of user
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfMemberships
     *            Number of result we want to get. Maximum number of result returned
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfUser(long userId, int fromIndex, int numberOfMemberships) throws SIdentityException;

    /**
     * Get userMemberships in a specific interval for a user in specific order, this is used for pagination
     * 
     * @param userId
     *            The identifier of user
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfMemberships
     *            Number of result we want to get. Maximum number of result returned
     * @param field
     *            The field user to do order
     * @param order
     *            ASC or DESC
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfUser(long userId, int fromIndex, int numberOfMemberships, String field, OrderByType order)
            throws SIdentityException;

    /**
     * @param userId
     *            The identifier of user
     * @param fromIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberPerPage
     *            Number of result we want to get. Maximum number of result returned
     * @param orderByOption
     *            OrderByOption object containing order information
     * @return a list of SUserMembership objects
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfUser(long userId, int fromIndex, int numberPerPage, OrderByOption orderByOption) throws SIdentityException;

    /**
     * Get all userMemberships for specific user
     * 
     * @param userId
     *            The identifier of user
     * @return a list of SUserMembership objects
     * @throws SUserNotFoundException
     *             occurs if no user has id corresponding to parameter userId
     * @throws SIdentityException
     */
    List<SUserMembership> getUserMembershipsOfUser(long userId) throws SIdentityException;

    /**
     * Get total number of userMemberships for specific user
     * 
     * @param userId
     *            The identifier of user
     * @return total number of userMemberships for the specific user
     * @throws SIdentityException
     */
    long getNumberOfUserMembershipsOfUser(long userId) throws SIdentityException;

    /**
     * Get total number of userMemberships
     * 
     * @return total number of userMemberships
     * @throws SIdentityException
     */
    long getNumberOfUserMemberships() throws SIdentityException;

    /**
     * Create user in DB for give user
     * 
     * @param user
     *            The user object
     * @return the created use
     * @throws SUserCreationException
     */
    SUser createUser(SUser user) throws SUserCreationException;

    /**
     * Update user according to the descriptor
     * 
     * @param user
     *            The user will be updated
     * @param descriptor
     *            The update description
     * @throws SUserUpdateException
     */
    void updateUser(SUser user, EntityUpdateDescriptor descriptor) throws SUserUpdateException;

    /**
     * Update user according to the descriptor
     * 
     * @param user
     *            The user will be updated
     * @param descriptor
     *            The update description
     * @param isPasswordEncrypted
     * @throws SUserUpdateException
     */
    @Deprecated
    void updateUser(SUser user, EntityUpdateDescriptor descriptor, boolean isPasswordEncrypted) throws SUserUpdateException;

    /**
     * Create profileMetadataDefinition in DB for give profileMetadataDefinition
     * 
     * @param metadata
     *            SProfileMetadataDefinition object
     * @throws SIdentityException
     */
    void createProfileMetadataDefinition(SProfileMetadataDefinition metadata) throws SIdentityException;

    /**
     * Update profileMetadataDefinition according to the descriptor
     * 
     * @param metadata
     *            The profileMetadataDefinition will be updated
     * @param descriptor
     *            The update description
     * @throws SIdentityException
     */
    void updateProfileMetadataDefinition(SProfileMetadataDefinition metadata, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Create profileMetadataValue in DB for give profileMetadataValue object
     * 
     * @param metadataValue
     *            A profileMetadataValue object
     * @throws SIdentityException
     */
    void createProfileMetadataValue(SProfileMetadataValue metadataValue) throws SIdentityException;

    /**
     * Update profileMetadataValue according to the descriptor
     * 
     * @param metadataValue
     *            The profileMetadataValue will be updated
     * @param descriptor
     *            The update description
     * @throws SIdentityException
     */
    void updateProfileMetadataValue(SProfileMetadataValue metadataValue, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Create role in DB for the given role
     * 
     * @param role
     *            A role object
     * @throws SIdentityException
     */
    void createRole(SRole role) throws SIdentityException;

    /**
     * Update role according to the descriptor
     * 
     * @param role
     *            The role will be updated
     * @param descriptor
     *            The update description
     * @throws SIdentityException
     */
    void updateRole(SRole role, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Create group in DB for the given group object
     * 
     * @param group
     *            A group object
     * @throws SGroupCreationException
     */
    void createGroup(SGroup group) throws SGroupCreationException;

    /**
     * Update group according to the descriptor
     * 
     * @param group
     *            The group will be updated
     * @param descriptor
     *            The update description
     * @throws SIdentityException
     */
    void updateGroup(SGroup group, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Create userMembership in DB for the given userMembership object
     * 
     * @param userMembership
     *            A userMembership object
     * @throws SUserMembershipCreationException
     */
    void createUserMembership(SUserMembership userMembership) throws SUserMembershipCreationException;

    /**
     * Update userMembership according to the descriptor
     * 
     * @param userMembership
     *            The userMembership will be updated
     * @param descriptor
     *            The update description
     * @throws SIdentityException
     */
    void updateUserMembership(SUserMembership userMembership, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Delete the specific user
     * 
     * @param user
     *            The user will be deleted
     * @throws SUserDeletionException
     */
    void deleteUser(SUser user) throws SUserDeletionException;

    /**
     * Delete user by its id
     * 
     * @param userId
     *            The identifier of user
     * @throws SUserDeletionException
     */
    void deleteUser(long userId) throws SUserDeletionException;

    /**
     * Delete all users for the connected tenant
     * 
     * @throws SUserDeletionException
     * @since 6.1
     */
    void deleteAllUsers() throws SUserDeletionException;

    /**
     * Delete the specific profileMetadataDefinition
     * 
     * @param metadataDefinition
     *            The profileMetadataDefinition object will be deleted
     * @throws SIdentityException
     */
    void deleteProfileMetadataDefinition(SProfileMetadataDefinition metadataDefinition) throws SIdentityException;

    /**
     * Delete the id specified profileMetadataDefinition
     * 
     * @param metadataDefinitionId
     *            The identifier of profileMetadataDefinition
     * @throws SIdentityException
     */
    void deleteProfileMetadataDefinition(long metadataDefinitionId) throws SIdentityException;

    /**
     * Delete the specific profileMetadataValue
     * 
     * @param metadataValue
     *            The profileMetadataValue object will be deleted
     * @throws SIdentityException
     */
    void deleteProfileMetadataValue(SProfileMetadataValue metadataValue) throws SIdentityException;

    /**
     * Delete the id specified profileMetadataValue
     * 
     * @param metadataValueId
     *            The identifier of profileMetadataValue
     * @throws SIdentityException
     */
    void deleteProfileMetadataValue(long metadataValueId) throws SIdentityException;

    /**
     * Delete the specific role
     * 
     * @param role
     *            The role will be deleted
     * @throws SRoleDeletionException
     */
    void deleteRole(SRole role) throws SRoleDeletionException;

    /**
     * Delete the id specified role
     * 
     * @param roleId
     *            The role identifier
     * @throws SRoleNotFoundException
     *             Error occurs when no role found with the specific roleId
     * @throws SRoleDeletionException
     */
    void deleteRole(long roleId) throws SRoleNotFoundException, SRoleDeletionException;

    /**
     * Delete all roles for the connected tenant
     * 
     * @throws SRoleDeletionException
     * @since 6.1
     */
    void deleteAllRoles() throws SRoleDeletionException;

    /**
     * Delete the specific group
     * 
     * @param group
     *            The group will be deleted
     * @throws SGroupDeletionException
     */
    void deleteGroup(SGroup group) throws SGroupDeletionException;

    /**
     * Delete the id specified group
     * 
     * @param groupId
     *            The identifier of group
     * @throws SGroupNotFoundException
     *             Error occurs when no group found with the specific groupId
     * @throws SGroupDeletionException
     */
    void deleteGroup(long groupId) throws SGroupNotFoundException, SGroupDeletionException;

    /**
     * Delete all groups for the connected tenant
     * 
     * @throws SGroupDeletionException
     * @since 6.1
     */
    void deleteAllGroups() throws SGroupDeletionException;

    /**
     * Delete the specific userMembership
     * 
     * @param userMembership
     *            The userMembership will be deleted
     * @throws SMembershipDeletionException
     */
    void deleteUserMembership(SUserMembership userMembership) throws SMembershipDeletionException;

    /**
     * Delete the specific light userMembership
     * 
     * @param userMembership
     * @throws SMembershipDeletionException
     * @since 6.1
     */
    void deleteLightUserMembership(SUserMembership userMembership) throws SMembershipDeletionException;

    /**
     * Delete the id specified userMembership
     * 
     * @param userMembershipId
     *            The identifier of userMembership
     * @throws SMembershipDeletionException
     */
    void deleteUserMembership(long userMembershipId) throws SMembershipDeletionException;

    /**
     * Delete all user memberships for the connected tenant
     * 
     * @throws SMembershipDeletionException
     * @since 6.1
     */
    void deleteAllUserMemberships() throws SMembershipDeletionException;

    /**
     * Get total number of users according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return the satisfied user number
     * @throws SBonitaSearchException
     */
    long getNumberOfUsers(QueryOptions options) throws SBonitaSearchException;

    /**
     * Search users according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return a list of SUser objects
     * @throws SBonitaSearchException
     */
    List<SUser> searchUsers(QueryOptions options) throws SBonitaSearchException;

    /**
     * Get total number of roles according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return the satisfied role number
     * @throws SBonitaSearchException
     */
    long getNumberOfRoles(QueryOptions options) throws SBonitaSearchException;

    /**
     * Search roles according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return a list of SRole objects
     * @throws SBonitaSearchException
     */
    List<SRole> searchRoles(QueryOptions options) throws SBonitaSearchException;

    /**
     * Get total number of groups according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return the group number
     * @throws SBonitaSearchException
     */
    long getNumberOfGroups(QueryOptions options) throws SBonitaSearchException;

    /**
     * Search groups according to specific query options
     * 
     * @param options
     *            The QueryOptions object containing some query conditions
     * @return a list of SGroup objects
     * @throws SBonitaSearchException
     */
    List<SGroup> searchGroups(QueryOptions options) throws SBonitaSearchException;

    /**
     * Get total number of userMemberships contains specific group and role
     * 
     * @param groupId
     *            The identifier of group
     * @param roleId
     *            The identifier of role
     * @return the number of userMemberships
     * @throws SIdentityException
     */
    long getNumberOfUsersByMembership(long groupId, long roleId) throws SIdentityException;

    /**
     * Get light userMembership by its id
     * 
     * @param userMembershipId
     *            The identifier of userMembership
     * @return a SUserMembership object without userName, groupName and roleName
     * @throws SIdentityException
     */
    SUserMembership getLightUserMembership(long userMembershipId) throws SIdentityException;

    /**
     * Get light userMembership in a specific interval, this is used for pagination
     * 
     * @param startIndex
     *            Index of the record to be retrieved from. First record has index 0
     * @param numberOfElements
     *            Number of result we want to get. Maximum number of result returned.
     * @return a list of SUserMembership objects without userName, groupName and roleName
     * @throws SIdentityException
     */
    List<SUserMembership> getLightUserMemberships(int startIndex, int numberOfElements) throws SIdentityException;

    /**
     * delete children groups of the given group if there is some
     * 
     * @param groupId
     *            The index of the group to delete
     * @throws SGroupDeletionException
     * @throws SGroupNotFoundException
     */
    List<Long> deleteChildrenGroup(long groupId) throws SGroupDeletionException, SGroupNotFoundException;

    /**
     * Return the user contact info for a specific user.
     * 
     * @param userId
     *            the ID of the user to retrieve the contact info from
     * @param isPersonal
     *            Do we want personal contact information (or professional) ?
     * @return the corresponding SContactInfo, if found
     * @throws SIdentityException
     *             if a Read problem occurred
     */
    SContactInfo getUserContactInfo(long userId, boolean isPersonal) throws SIdentityException;

    /**
     * Create user contact information for given data
     * 
     * @param contactInfo
     *            The user contact information object
     * @return
     *         The contact info created
     * @throws SUserCreationException
     */
    SContactInfo createUserContactInfo(SContactInfo contactInfo) throws SUserCreationException;

    /**
     * Update user contact information according to the descriptor
     * 
     * @param contactInfo
     *            The user contact information to be updated
     * @param descriptor
     *            The update description
     * @throws SUserUpdateException
     */
    void updateUserContactInfo(SContactInfo contactInfo, EntityUpdateDescriptor descriptor) throws SIdentityException;

    /**
     * Create user in DB for given user and
     * 
     * @param user
     *            The user object
     * @param passwordEncrypted
     * @throws SUserCreationException
     */
    @Deprecated
    SUser createUserWithoutEncryptingPassword(SUser user) throws SUserCreationException;

}

/**
 * Copyright (C) 2012-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.identity.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.identity.ExportedUser;
import org.bonitasoft.engine.identity.Group;
import org.bonitasoft.engine.identity.Role;
import org.bonitasoft.engine.identity.UserMembership;
import org.bonitasoft.engine.xml.ElementBinding;
import org.bonitasoft.engine.xml.XMLNode;

/**
 * @author Yanyan Liu
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 */
public class OrganizationNodeBuilder {

    static final List<Class<? extends ElementBinding>> BINDINGS = new ArrayList<Class<? extends ElementBinding>>();

    private static final String NAMESPACE = "http://www.bonitasoft.org/ns/organization/6.0.0-beta-016";

    private static final String NS_PREFIX = "organization";

    static {
        BINDINGS.add(OrganizationBinding.class);
        BINDINGS.add(UserBinding.class);
        BINDINGS.add(PersonalContactDataBinding.class);
        BINDINGS.add(ProfessionalContactDataBinding.class);
        BINDINGS.add(RoleBinding.class);
        BINDINGS.add(GroupBinding.class);
        BINDINGS.add(MembershipBinding.class);
    }

    public static XMLNode getDocument(final List<ExportedUser> users, final Map<Long, String> userNames, final List<Group> groups,
            final Map<Long, String> groupIdParentPath, final List<Role> roles, final List<UserMembership> userMemberships) {
        final XMLNode document = getRootNode();

        final XMLNode usersNode = new XMLNode(OrganizationMappingConstants.USERS);
        document.addChild(usersNode);
        for (final ExportedUser user : users) {
            final XMLNode userNode = getUserNode(user);
            usersNode.addChild(userNode);
        }

        final XMLNode rolesNodes = new XMLNode(OrganizationMappingConstants.ROLES);
        document.addChild(rolesNodes);
        for (final Role role : roles) {
            final XMLNode roleNode = getRoleNode(role);
            rolesNodes.addChild(roleNode);
        }

        final XMLNode groupsNode = new XMLNode(OrganizationMappingConstants.GROUPS);
        document.addChild(groupsNode);
        for (final Group group : groups) {
            final XMLNode groupNode = getGroupNode(group);
            groupsNode.addChild(groupNode);
        }

        final XMLNode membershipsNode = new XMLNode(OrganizationMappingConstants.MEMBERSHIPS);
        document.addChild(membershipsNode);
        for (final UserMembership userMembership : userMemberships) {
            final XMLNode membershipNode = getMembershipNode(userNames, groupIdParentPath, userMembership);
            membershipsNode.addChild(membershipNode);
        }
        return document;
    }

    private static XMLNode getMembershipNode(final Map<Long, String> userNames, final Map<Long, String> groupIdParentPath, final UserMembership membership) {
        final XMLNode membershipNode = new XMLNode(OrganizationMappingConstants.MEMBERSHIP);
        XMLNode node = new XMLNode(OrganizationMappingConstants.USER_NAME);
        node.setContent(String.valueOf(membership.getUsername()));
        membershipNode.addChild(node);
        node = new XMLNode(OrganizationMappingConstants.ROLE_NAME);
        node.setContent(membership.getRoleName());
        membershipNode.addChild(node);
        node = new XMLNode(OrganizationMappingConstants.GROUP_NAME);
        node.setContent(membership.getGroupName());
        membershipNode.addChild(node);
        node = new XMLNode(OrganizationMappingConstants.GROUP_PARENT_PATH);
        node.setContent(groupIdParentPath.get(membership.getGroupId()));
        membershipNode.addChild(node);
        if (membership.getAssignedBy() > 0) {
            node = new XMLNode(OrganizationMappingConstants.ASSIGNED_BY);
            node.setContent(userNames.get(membership.getAssignedBy()));
            membershipNode.addChild(node);
        }
        if (membership.getAssignedDate() != null && membership.getAssignedDate().getTime() > 0) {
            node = new XMLNode(OrganizationMappingConstants.ASSIGNED_DATE);
            node.setContent(String.valueOf(membership.getAssignedDate().getTime()));
            membershipNode.addChild(node);
        }
        return membershipNode;
    }

    private static XMLNode getRoleNode(final Role role) {
        final XMLNode roleNode = new XMLNode(OrganizationMappingConstants.ROLE);
        if (role.getName() != null) {
            roleNode.addAttribute(OrganizationMappingConstants.NAME, role.getName());
        }
        if (role.getDisplayName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.DISPLAY_NAME);
            node.setContent(role.getDisplayName());
            roleNode.addChild(node);
        }
        if (role.getDescription() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.DESCRIPTION);
            node.setContent(role.getDescription());
            roleNode.addChild(node);
        }
        if (role.getIconName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_NAME);
            node.setContent(role.getIconName());
            roleNode.addChild(node);
        }
        if (role.getIconPath() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_PATH);
            node.setContent(role.getIconPath());
            roleNode.addChild(node);
        }
        return roleNode;
    }

    private static XMLNode getGroupNode(final Group group) {
        final XMLNode groupNode = new XMLNode(OrganizationMappingConstants.GROUP);
        if (group.getName() != null) {
            groupNode.addAttribute(OrganizationMappingConstants.NAME, group.getName());
        }
        if (group.getParentPath() != null) {
            groupNode.addAttribute(OrganizationMappingConstants.PARENT_PATH, group.getParentPath());
        }
        if (group.getDisplayName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.DISPLAY_NAME);
            node.setContent(group.getDisplayName());
            groupNode.addChild(node);
        }
        if (group.getDescription() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.DESCRIPTION);
            node.setContent(group.getDescription());
            groupNode.addChild(node);
        }
        if (group.getIconName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_NAME);
            node.setContent(group.getIconName());
            groupNode.addChild(node);
        }
        if (group.getIconPath() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_PATH);
            node.setContent(group.getIconPath());
            groupNode.addChild(node);
        }
        return groupNode;
    }

    private static XMLNode getUserNode(final ExportedUser user) {
        final XMLNode userNode = new XMLNode(OrganizationMappingConstants.USER);
        userNode.addAttribute(OrganizationMappingConstants.USER_NAME, user.getUserName());
        final XMLNode nodePassword = new XMLNode(OrganizationMappingConstants.PASSWORD);
        nodePassword.addAttribute(OrganizationMappingConstants.PASSWORD_ENCRYPTED, "true");
        nodePassword.setContent(user.getPassword());
        userNode.addChild(nodePassword);
        if (user.getFirstName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.FIRST_NAME);
            node.setContent(user.getFirstName());
            userNode.addChild(node);
        }
        if (user.getLastName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.LAST_NAME);
            node.setContent(user.getLastName());
            userNode.addChild(node);
        }
        if (user.getIconName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_NAME);
            node.setContent(user.getIconName());
            userNode.addChild(node);
        }
        if (user.getIconPath() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.ICON_PATH);
            node.setContent(user.getIconPath());
            userNode.addChild(node);
        }
        if (user.getTitle() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.TITLE);
            node.setContent(user.getTitle());
            userNode.addChild(node);
        }
        if (user.getJobTitle() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.JOB_TITLE);
            node.setContent(user.getJobTitle());
            userNode.addChild(node);
        }
        final XMLNode enabledNode = new XMLNode(OrganizationMappingConstants.ENABLED);
        enabledNode.setContent(user.isEnabled() ? "true" : "false");
        userNode.addChild(enabledNode);

        final XMLNode personalDataNode = getPersonalDataNode(user);
        userNode.addChild(personalDataNode);

        final XMLNode professionalDataNode = getProfessionalDataNode(user);
        userNode.addChild(professionalDataNode);

        if (user.getManagerUserName() != null) {
            final XMLNode node = new XMLNode(OrganizationMappingConstants.MANAGER);
            node.setContent(user.getManagerUserName());
            userNode.addChild(node);
        }
        return userNode;
    }

    private static XMLNode getPersonalDataNode(final ExportedUser user) {
        final XMLNode contactDataNode = new XMLNode(OrganizationMappingConstants.PERSONAL_DATA);
        if (user.getPersonalAddress() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ADDRESS, user.getPersonalAddress());
        }
        if (user.getPersonalBuilding() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.BUILDING, user.getPersonalBuilding());
        }
        if (user.getPersonalCity() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.CITY, user.getPersonalCity());
        }
        if (user.getPersonalCountry() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.COUNTRY, user.getPersonalCountry());
        }
        if (user.getPersonalEmail() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.EMAIL, user.getPersonalEmail());
        }
        if (user.getPersonalFaxNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.FAX_NUMBER, user.getPersonalFaxNumber());
        }
        if (user.getPersonalMobileNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.MOBILE_NUMBER, user.getPersonalMobileNumber());
        }
        if (user.getPersonalPhoneNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.PHONE_NUMBER, user.getPersonalPhoneNumber());
        }
        if (user.getPersonalRoom() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ROOM, user.getPersonalRoom());
        }
        if (user.getPersonalState() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.STATE, user.getPersonalState());
        }
        if (user.getPersonalWebsite() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.WEBSITE, user.getPersonalWebsite());
        }
        if (user.getPersonalZipCode() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ZIP_CODE, user.getPersonalZipCode());
        }
        return contactDataNode;
    }

    private static XMLNode getProfessionalDataNode(final ExportedUser user) {
        final XMLNode contactDataNode = new XMLNode(OrganizationMappingConstants.PROFESSIONAL_DATA);
        if (user.getProfessionalAddress() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ADDRESS, user.getProfessionalAddress());
        }
        if (user.getProfessionalBuilding() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.BUILDING, user.getProfessionalBuilding());
        }
        if (user.getProfessionalCity() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.CITY, user.getProfessionalCity());
        }
        if (user.getProfessionalCountry() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.COUNTRY, user.getProfessionalCountry());
        }
        if (user.getProfessionalEmail() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.EMAIL, user.getProfessionalEmail());
        }
        if (user.getProfessionalFaxNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.FAX_NUMBER, user.getProfessionalFaxNumber());
        }
        if (user.getProfessionalMobileNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.MOBILE_NUMBER, user.getProfessionalMobileNumber());
        }
        if (user.getProfessionalPhoneNumber() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.PHONE_NUMBER, user.getProfessionalPhoneNumber());
        }
        if (user.getProfessionalRoom() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ROOM, user.getProfessionalRoom());
        }
        if (user.getProfessionalState() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.STATE, user.getProfessionalState());
        }
        if (user.getProfessionalWebsite() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.WEBSITE, user.getProfessionalWebsite());
        }
        if (user.getProfessionalZipCode() != null) {
            contactDataNode.addChild(OrganizationMappingConstants.ZIP_CODE, user.getProfessionalZipCode());
        }
        return contactDataNode;
    }

    private static XMLNode getRootNode() {
        final XMLNode organizationNode = new XMLNode(NS_PREFIX + ":" + OrganizationMappingConstants.IDENTITY_ORGANIZATION);
        organizationNode.addAttribute("xmlns:" + NS_PREFIX, NAMESPACE);
        return organizationNode;
    }

}

package au.com.auspost.startrack_corp.core.common;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

public class NodeImpl implements javax.jcr.Node {
    @Override
    public void accept(ItemVisitor arg0) throws RepositoryException {}
    @Override
    public Item getAncestor(int arg0) throws ItemNotFoundException, AccessDeniedException, RepositoryException { return null; }
    @Override
    public int getDepth() throws RepositoryException { return 0; }
    @Override
    public String getName() throws RepositoryException { return null; }
    @Override
    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException { return null; }
    @Override
    public String getPath() throws RepositoryException { return null; }
    @Override
    public Session getSession() throws RepositoryException { return null; }
    @Override
    public boolean isModified() { return false; }
    @Override
    public boolean isNew() { return false; }
    @Override
    public boolean isNode() { return false; }
    @Override
    public boolean isSame(Item arg0) throws RepositoryException { return false; }
    @Override
    public void refresh(boolean arg0) throws InvalidItemStateException, RepositoryException {}
    @Override
    public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {}
    @Override
    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException,
        ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {}
    @Override
    public void addMixin(String arg0) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException,
        LockException, RepositoryException {}
    @Override
    public Node addNode(String arg0) throws ItemExistsException, PathNotFoundException, VersionException,
        ConstraintViolationException, LockException, RepositoryException { return null; }
    @Override
    public Node addNode(String arg0, String arg1) throws ItemExistsException, PathNotFoundException,
        NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException { return null; }
    @Override
    public boolean canAddMixin(String arg0) throws NoSuchNodeTypeException, RepositoryException { return false; }
    @Override
    public void cancelMerge(Version arg0) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException {}

    @Override
    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException,
        InvalidItemStateException, LockException, RepositoryException { return null; }

    @Override
    public void checkout()
        throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException, RepositoryException {}

    @Override
    public void doneMerge(Version arg0) throws VersionException, InvalidItemStateException,
        UnsupportedRepositoryOperationException, RepositoryException {}

    @Override
    public void followLifecycleTransition(String arg0)
        throws UnsupportedRepositoryOperationException, InvalidLifecycleTransitionException, RepositoryException {}

    @Override
    public String[] getAllowedLifecycleTransistions()
        throws UnsupportedRepositoryOperationException, RepositoryException { return null; }

    @Override
    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException { return null; }

    @Override
    public String getCorrespondingNodePath(String arg0)
        throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException { return null; }

    @Override
    public NodeDefinition getDefinition() throws RepositoryException { return null; }

    @Override
    public String getIdentifier() throws RepositoryException { return null; }

    @Override
    public int getIndex() throws RepositoryException { return 0; }

    @Override
    public Lock getLock()
        throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException { return null; }

    @Override
    public NodeType[] getMixinNodeTypes() throws RepositoryException { return null; }

    @Override
    public Node getNode(String arg0) throws PathNotFoundException, RepositoryException { return null; }

    @Override
    public NodeIterator getNodes() throws RepositoryException { return null; }

    @Override
    public NodeIterator getNodes(String arg0) throws RepositoryException { return null; }

    @Override
    public NodeIterator getNodes(String[] arg0) throws RepositoryException { return null; }

    @Override
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException { return null; }

    @Override
    public NodeType getPrimaryNodeType() throws RepositoryException { return null; }

    @Override
    public PropertyIterator getProperties() throws RepositoryException {
        return new PropertyIteratorImpl();
    }

    @Override
    public PropertyIterator getProperties(String arg0) throws RepositoryException { return null; }

    @Override
    public PropertyIterator getProperties(String[] arg0) throws RepositoryException { return null; }

    @Override
    public Property getProperty(String arg0) throws PathNotFoundException, RepositoryException { return null; }

    @Override
    public PropertyIterator getReferences() throws RepositoryException { return null; }

    @Override
    public PropertyIterator getReferences(String arg0) throws RepositoryException { return null; }

    @Override
    public NodeIterator getSharedSet() throws RepositoryException { return null; }

    @Override
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException { return null; }

    @Override
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException { return null; }

    @Override
    public PropertyIterator getWeakReferences() throws RepositoryException { return null; }

    @Override
    public PropertyIterator getWeakReferences(String arg0) throws RepositoryException { return null; }

    @Override
    public boolean hasNode(String arg0) throws RepositoryException { return false; }

    @Override
    public boolean hasNodes() throws RepositoryException { return false; }

    @Override
    public boolean hasProperties() throws RepositoryException { return false; }

    @Override
    public boolean hasProperty(String arg0) throws RepositoryException { return false; }

    @Override
    public boolean holdsLock() throws RepositoryException { return false; }

    @Override
    public boolean isCheckedOut() throws RepositoryException { return false; }

    @Override
    public boolean isLocked() throws RepositoryException { return false; }

    @Override
    public boolean isNodeType(String arg0) throws RepositoryException { return false; }

    @Override
    public Lock lock(boolean arg0, boolean arg1) throws UnsupportedRepositoryOperationException, LockException,
        AccessDeniedException, InvalidItemStateException, RepositoryException { return null; }

    @Override
    public NodeIterator merge(String arg0, boolean arg1) throws NoSuchWorkspaceException, AccessDeniedException,
        MergeException, LockException, InvalidItemStateException, RepositoryException { return null; }

    @Override
    public void orderBefore(String arg0, String arg1) throws UnsupportedRepositoryOperationException, VersionException,
        ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {}

    @Override
    public void removeMixin(String arg0) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException,
        LockException, RepositoryException {}

    @Override
    public void removeShare()
        throws VersionException, LockException, ConstraintViolationException, RepositoryException {}

    @Override
    public void removeSharedSet()
        throws VersionException, LockException, ConstraintViolationException, RepositoryException {}

    @Override
    public void restore(String arg0, boolean arg1) throws VersionException, ItemExistsException,
        UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {}

    @Override
    public void restore(Version arg0, boolean arg1) throws VersionException, ItemExistsException,
        InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException {}

    @Override
    public void restore(Version arg0, String arg1, boolean arg2)
        throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException,
        UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {}

    @Override
    public void restoreByLabel(String arg0, boolean arg1) throws VersionException, ItemExistsException,
        UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {}

    @Override
    public void setPrimaryType(String arg0) throws NoSuchNodeTypeException, VersionException,
        ConstraintViolationException, LockException, RepositoryException {}

    @Override
    public Property setProperty(String arg0, Value arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Value[] arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, String[] arg1) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, String arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, InputStream arg1) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Binary arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, boolean arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, double arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, BigDecimal arg1) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, long arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Calendar arg1) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Node arg1) throws ValueFormatException, VersionException, LockException,
        ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Value arg1, int arg2) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, Value[] arg1, int arg2) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, String[] arg1, int arg2) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public Property setProperty(String arg0, String arg1, int arg2) throws ValueFormatException, VersionException,
        LockException, ConstraintViolationException, RepositoryException { return null; }

    @Override
    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException,
        InvalidItemStateException, RepositoryException {}

    @Override
    public void update(String arg0) throws NoSuchWorkspaceException, AccessDeniedException, LockException,
        InvalidItemStateException, RepositoryException {}
}

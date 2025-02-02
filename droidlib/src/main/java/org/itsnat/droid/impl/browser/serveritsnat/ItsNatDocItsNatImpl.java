package org.itsnat.droid.impl.browser.serveritsnat;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.itsnat.droid.ClientErrorMode;
import org.itsnat.droid.EventMonitor;
import org.itsnat.droid.HttpRequestResult;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.ItsNatDroidServerResponseException;
import org.itsnat.droid.ItsNatView;
import org.itsnat.droid.OnEventErrorListener;
import org.itsnat.droid.OnServerStateLostListener;
import org.itsnat.droid.event.Event;
import org.itsnat.droid.event.EventStateless;
import org.itsnat.droid.event.UserEvent;
import org.itsnat.droid.impl.browser.PageItsNatImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.AttachedClientCometTaskRefreshEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.AttachedClientTimerRefreshEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.AttachedClientUnloadEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DOMExtEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DroidFocusEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DroidKeyEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DroidMotionEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DroidOtherEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DroidTextChangeEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.EventStatelessImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.UserEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.AsyncTaskEventListener;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.CometTaskEventListener;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.ContinueEventListener;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.DroidEventListener;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.TimerEventListener;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.UserEventListener;
import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.util.MapList;
import org.itsnat.droid.impl.util.MapListLight;
import org.itsnat.droid.impl.util.MapListReal;
import org.itsnat.droid.impl.util.NameValue;
import org.itsnat.droid.impl.xmlinflater.XMLInflateRegistry;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.ClassDescViewMgr;
import org.itsnat.droid.impl.xmlinflater.layout.PendingPostInsertChildrenTasks;
import org.itsnat.droid.impl.xmlinflater.layout.PendingViewPostCreateProcess;
import org.itsnat.droid.impl.xmlinflater.layout.XMLInflaterLayout;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;
import org.itsnat.droid.impl.xmlinflater.layout.page.XMLInflaterLayoutPage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jmarranz on 23/12/2015.
 */
public class ItsNatDocItsNatImpl extends ItsNatDocImpl implements ItsNatDocItsNatPublic
{
    private static final String key_itsNatUserListenersByName = "itsNatUserListenersByName";

    protected String itsNatServletPath; // Definido en el servidor
    protected Map<String,Node> nodeCacheById = new HashMap<String,Node>();
    protected DOMPathResolver pathResolver = new DOMPathResolverImpl(this);
    protected Map<String,DroidEventListener> droidEventListeners;
    protected Map<String,TimerEventListener> timerEventListeners;
    protected Map<String,UserEventListener> userListenersById;
    protected MapList<String,UserEventListener> userListenersByName;
    protected Runnable attachTimerRefreshCallback;
    protected Runnable attachUnloadCallback;
    protected List<GlobalEventListener> globalEventListeners;
    protected String attachType;
    protected boolean eventsEnabled;
    protected boolean disabledEvents = false; // En Droid tiene poco sentido y no se usa, candidato a eliminarse
    protected boolean enableEvtMonitors = true;
    protected List<EventMonitor> evtMonitorList;
    protected EventManager evtManager = new EventManager(this);


    public ItsNatDocItsNatImpl(PageItsNatImpl page,int errorMode)
    {
        super(page,errorMode); // errorMode el valor inicial, será cambiado por el método init() (si hay scripting)
    }

    @Override
    public void init(String stdSessionId,String sessionToken,String sessionId,String clientId,String servletPath,int errorMode,String attachType,boolean eventsEnabled)
    {
        if (errorMode == ClientErrorMode.NOT_CATCH_ERRORS)
            throw new ItsNatDroidException("ClientErrorMode.NOT_CATCH_ERRORS is not supported"); // No tiene mucho sentido porque el objetivo es dejar fallar y si el usuario no ha registrado "error listeners" ItsNat Droid deja siempre fallar lanzando la excepción

        ((PageItsNatImpl)page).setSessionIdAndClientId(stdSessionId, sessionToken, sessionId, clientId);
        this.itsNatServletPath = servletPath;
        this.errorMode = errorMode; // Modifica el valor inicial
        this.attachType = attachType;
        this.eventsEnabled = eventsEnabled;
    }

    public String getItsNatServletPath()
    {
        return itsNatServletPath;
    }

    public EventManager getEventManager()
    {
        return evtManager;
    }



    public Runnable getAttachTimerRefreshCallback()
    {
        return attachTimerRefreshCallback;
    }

    public String getAttachType()
    {
        return attachType;
    }



    public boolean isEventsEnabled()
    {
        return eventsEnabled;
    }


    public boolean isDisabledEvents()
    {
        return disabledEvents;
    }


    @Override
    public void setDisabledEvents()
    {
        this.disabledEvents = true;
    }

    @Override
    public void onServerStateLost()
    {
        OnServerStateLostListener listener = page.getOnServerStateLostListener();
        if (listener != null) listener.onServerStateLost(page);
    }

    @Override
    public void setEnableEventMonitors(boolean value) { this.enableEvtMonitors = value; }

    @Override
    public void addEventMonitor(EventMonitor monitor)
    {
        if (evtMonitorList == null) this.evtMonitorList = new LinkedList<EventMonitor>();
        evtMonitorList.add(monitor);
    }

    @Override
    public boolean removeEventMonitor(EventMonitor monitor)
    {
        if (evtMonitorList == null) return false;
        return evtMonitorList.remove(monitor);
    }

    public void fireEventMonitors(boolean before,boolean timeout,Event evt)
    {
        if (!this.enableEvtMonitors) return;

        if (evtMonitorList == null) return;

        for(EventMonitor curr : evtMonitorList)
        {
            if (before) curr.before(evt);
            else curr.after(evt,timeout);
        }
    }




    public List<NameValue> genParamURL()
    {
        PageItsNatImpl pageItsNat = (PageItsNatImpl)page;
        List<NameValue> paramList = new LinkedList<NameValue>();
        paramList.add(new NameValue("itsnat_client_id", pageItsNat.getId()));
        paramList.add(new NameValue("itsnat_session_token", pageItsNat.getItsNatSessionImpl().getToken()));
        paramList.add(new NameValue("itsnat_session_id", pageItsNat.getItsNatSessionImpl().getId()));
        return paramList;
    }

    private static String getPrefix(String name)
    {
        int pos = name.indexOf(':');
        if (pos == -1) return null;
        return name.substring(0,pos);
    }

    private static String getLocalName(String name)
    {
        int pos = name.indexOf(':');
        if (pos == -1) return name;
        return name.substring(pos + 1);
    }


    private void setAttributeNSInternal(Node node,String namespaceURI,String name,String value,ClassDescViewBased viewClassDesc,AttrLayoutContext attrCtx)
    {
        if (namespaceURI == null)
        {
            String prefix = getPrefix(name);
            if (prefix != null)
            {
                namespaceURI = getPageImpl().getInflatedLayoutPageImpl().getNamespace(prefix);
                if (namespaceURI != null) // Sólo se soportan namespaces declarados en el View root, si es null se procesará como un atributo desconocido
                    name = getLocalName(name);
            }
        }

        DOMAttr attr = DOMAttr.create(namespaceURI, name, value);

        if (node instanceof NodeToInsertImpl)
        {
            NodeToInsertImpl nodeToIn = (NodeToInsertImpl)node;
            if (!nodeToIn.isInserted())
            {
                nodeToIn.setAttribute(attr);
                return;
            }
        }

        View view = node.getView();
        page.getXMLInflaterLayoutPage().setAttributeFromRemote(view, attr, viewClassDesc, attrCtx);
    }

    @Override
    public void setAttribute(Node node,String name,String value)
    {
        setAttributeNS(node, null, name, value);
    }

    @Override
    public void setAttribute2(Object[] idObj,String name,String value)
    {
        setAttributeNS2(idObj, null, name, value);
    }

    @Override
    public void setAttributeNS(Node node,String namespaceURI,String name,String value)
    {
        setAttributeNSInternal(node, namespaceURI, name, value, null, null);
    }

    @Override
    public void setAttributeNS2(Object[] idObj,String namespaceURI,String name,String value)
    {
        Node elem = getNode(idObj);
        setAttributeNS(elem, namespaceURI, name, value);
    }

    @Override
    public void setAttrBatch(Node node,String namespaceURI,String[] attrNames,String[] attrValues)
    {
        View view = node.getView();

        ClassDescViewBased viewClassDesc = null;
        AttrLayoutContext attrCtx = null;
        PendingViewPostCreateProcess pendingViewPostCreateProcess = null;

        if (view != null) // Cuando Node es NodeToInsert el View es null pues no se ha creado, yo creo que nunca se llama a setAttrBatch con un nodo ya insertado pero intentamos optimizar por si cambiar ItsNat server eso ganamos
        {
            ClassDescViewMgr classDescViewMgr = getPageImpl().getItsNatDroidBrowserImpl().getItsNatDroidImpl().getXMLInflateRegistry().getClassDescViewMgr();
            viewClassDesc = classDescViewMgr.get(view);

            XMLInflaterLayoutPage xmlInflaterLayoutPage = page.getXMLInflaterLayoutPage();

            pendingViewPostCreateProcess = viewClassDesc.createPendingViewPostCreateProcess(view, (ViewGroup) view.getParent());
            attrCtx = new AttrLayoutContext(getContext(), xmlInflaterLayoutPage, pendingViewPostCreateProcess, null);
        }

        int len = attrNames.length;
        for(int i = 0; i < len; i++)
        {
            String name = attrNames[i];
            String value = attrValues[i];
            setAttributeNSInternal(node,namespaceURI,name,value,viewClassDesc,attrCtx);
        }

        if (pendingViewPostCreateProcess != null)
        {
            pendingViewPostCreateProcess.executePendingSetAttribsTasks();
            pendingViewPostCreateProcess.executePendingLayoutParamsTasks();
        }
    }

    @Override
    public void removeAttribute(Node node, String name)
    {
        removeAttributeNS(node, null, name);
    }

    @Override
    public void removeAttribute2(Object[] idObj, String name)
    {
        removeAttributeNS2(idObj, null, name);
    }

    @Override
    public void removeAttributeNS(Node node, String namespaceURI, String name)
    {
        if (namespaceURI == null)
        {
            String prefix = getPrefix(name);
            if (prefix != null)
            {
                namespaceURI = getPageImpl().getInflatedLayoutPageImpl().getNamespace(prefix);
                if (namespaceURI != null) // Sólo se soportan namespaces declarados en el View root, si es null se procesará como un atributo desconocido
                    name = getLocalName(name);
            }
        }

        if (node instanceof NodeToInsertImpl)
        {
            // Esto es raro pero es por si cambia de opinión para un atributo recién definido
            NodeToInsertImpl nodeToIn = (NodeToInsertImpl)node;
            if (!nodeToIn.isInserted())
            {
                nodeToIn.removeAttribute(namespaceURI, name);
                return;
            }
        }


        View view = node.getView();
        page.getXMLInflaterLayoutPage().removeAttributeFromRemote(view, namespaceURI, name);
    }

    @Override
    public void removeAttributeNS2(Object[] idObj, String namespaceURI, String name)
    {
        Node node = getNode(idObj);
        removeAttributeNS(node, namespaceURI, name);
    }

    @Override
    public View getView(Object[] idObj)
    {
        // Este método es llamado por ScriptUtil.getNodeReference(), el usuario espera que devuelva un View no nuestro Node wrapper
        Node node = getNode(idObj);
        if (node == null) return null;
        return node.getView();
    }

    @Override
    public Node getNode(Object[] idObj)
    {
        if (idObj == null) return null;
        String id = null;
        String cachedParentId = null;
        String path = null;
        Object[] newCachedParentIds = null;
        int len = idObj.length;
        if (len == 1)
        {
            id = (String)idObj[0];
        }
        else if (len == 2)
        {
            id = (String)idObj[0];
            newCachedParentIds = (Object[])idObj[1];
        }
        else if (len >= 3)
        {
            cachedParentId = (String)idObj[0];
            id =   (String)idObj[1];
            path = (String)idObj[2];
            if (len == 4) newCachedParentIds = (Object[])idObj[3];
        }
        return getNode(id,path,cachedParentId,newCachedParentIds);
    }


    private Node getNode(String id,String path,String cachedParentId,Object[] newCachedParentIds)
    {
        Node cachedParent = null;
        if (cachedParentId != null)
        {
            cachedParent = getNodeCached(cachedParentId);
            if (cachedParent == null) throw new ItsNatDroidException("Unexpected error");
        }

        Node node = getNode2(cachedParent, new Object[]{id, path});
        if (newCachedParentIds != null)
        {
            Node parentNode = getParentNode(node);
            for (Object newCachedParentId : newCachedParentIds)
            {
                addNodeCache2((String) newCachedParentId, parentNode);
                parentNode = getParentNode(parentNode);
            }
        }
        return node;
    }

    /*
    private Node getNode2(Node parentNode,String id)
    {
        return getNode2(parentNode,new Object[]{id});
    }
    */

    private Node getNode2(Node parentNode,Object[] idObj) // No es público
    {
        if (idObj == null) return null;
        String id;
        String path = null;

        id = (String)idObj[0];
        if (idObj.length == 2) path = (String)idObj[1];

        if ((id == null) && (path == null)) return null; // raro
        if (path == null) return getNodeCached(id); // Debe estar en la httpFileCache
        else
        {
            // si parentNode es null caso de path absoluto, si no, path relativo
            Node node = pathResolver.getNodeFromPath(path,parentNode);
            if (id != null) addNodeCache2(id,node);
            return node;
        }
    }

    private String getNodeCacheId(Node node)
    {
        View view = node.getView();
        ItsNatViewImpl itsNatView = getItsNatViewImpl(view);
        return itsNatView.getNodeCacheId();
    }

    private Node getNodeCached(String id) // No es público
    {
        if (id == null) return null;
        return nodeCacheById.get(id);
    }

    private void addNodeCache2(String id,Node node)
    {
        if (id == null) return; // si id es null httpFileCache desactivado
        nodeCacheById.put(id,node);
        View view = node.getView();
        ItsNatViewImpl itsNatView = getItsNatViewImpl(view);
        itsNatView.setNodeCacheId(id);
    }

    public Map<String,DroidEventListener> getDroidEventListeners()
    {
        if (droidEventListeners == null) this.droidEventListeners = new HashMap<String,DroidEventListener>();
        return droidEventListeners;
    }

    public Map<String,TimerEventListener> getTimerEventListeners()
    {
        if (timerEventListeners == null) this.timerEventListeners = new HashMap<String,TimerEventListener>();
        return timerEventListeners;
    }

    public Map<String,UserEventListener> getUserEventListenersById()
    {
        if (userListenersById == null) this.userListenersById = new HashMap<String,UserEventListener>();
        return userListenersById;
    }

    public MapList<String,UserEventListener> getUserEventListenersByName()
    {
        if (userListenersByName == null) this.userListenersByName = new MapListReal<String,UserEventListener>();
        return userListenersByName;
    }


    public Node getParentNode(Node node)
    {
        return NodeImpl.create((View) node.getView().getParent());
    }

    @Override
    public Node createElement(String name)
    {
        return createElementNS(null, name);
    }

    @Override
    public Node createElementNS(String namespaceURI,String name)
    {
        // El namespaceURI es irrelevante
        return new NodeToInsertImpl(name);
    }

    public String getStringPathFromView(View view)
    {
        return getStringPathFromNode(NodeImpl.create(view));
    }

    public String getStringPathFromNode(Node node)
    {
        if (node == null) return null;

        String nodeId = getNodeCacheId(node);
        if (nodeId != null) return "id:" + nodeId; // es undefined si no esta cacheado (o null si se quito)
        else
        {
            String parentId;
            Node parentNode = node;
            do
            {
                parentNode = getParentNode(parentNode);
                parentId = getNodeCacheId(parentNode); // si parentNode es null devuelve null
            }
            while((parentId == null)&&(parentNode != null));

            String path = pathResolver.getStringPathFromNode(node,parentNode); // Si parentNode es null (parentId es null) devuelve un path absoluto
            if (parentNode != null) return "pid:" + parentId + ":" + path;
            return path; // absoluto
        }
    }


    private static int getChildIndex(Node parentNode,Node node)
    {
        // Esto es una chapuza pero no hay opción
        ViewGroup parentView = (ViewGroup)parentNode.getView();
        View view = node.getView();
        int index = parentView.indexOfChild(view);
        return index;
    }

    private View createViewObjectAndFillAttributesAndAdd(ClassDescViewBased classDesc, ViewGroup viewParent, NodeToInsertImpl newChildToIn, int index,XMLInflaterLayout xmlInflaterLayout,PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        Context ctx = getContext();
        View view = classDesc.createViewObjectFromRemote(newChildToIn, pendingPostInsertChildrenTasks, ctx);

        newChildToIn.setView(view);

        PendingViewPostCreateProcess pendingViewPostCreateProcess = classDesc.createPendingViewPostCreateProcess(view, viewParent);
        AttrLayoutContext attrCtx = new AttrLayoutContext(getContext(),xmlInflaterLayout, pendingViewPostCreateProcess, pendingPostInsertChildrenTasks);

        fillViewAttributes(classDesc,newChildToIn,xmlInflaterLayout, attrCtx);
        classDesc.addViewObject(viewParent, view, index, pendingViewPostCreateProcess, getContext());

        pendingViewPostCreateProcess.destroy();

        return view;
    }


    private void fillViewAttributes(ClassDescViewBased classDesc,NodeToInsertImpl newChildToIn,XMLInflaterLayout xmlInflaterLayout,AttrLayoutContext attrCtx)
    {
        View view = newChildToIn.getView();

        if (newChildToIn.hasAttributes())
        {
            for (Map.Entry<String, DOMAttr> entry : newChildToIn.getAttributes().entrySet())
            {
                DOMAttr attr = entry.getValue();
                xmlInflaterLayout.setAttribute(classDesc, view, attr,attrCtx);
            }
        }

        attrCtx.getPendingViewPostCreateProcess().executePendingSetAttribsTasks();
    }

    @Override
    public void insertBefore(Node parentNode,Node newChild,Node childRef)
    {
        NodeToInsertImpl newChildToIn = (NodeToInsertImpl)newChild;

        XMLInflaterLayoutPage xmlInflaterLayout = page.getXMLInflaterLayoutPage();
        XMLInflateRegistry xmlInflateRegistry = page.getItsNatDroidBrowserImpl().getItsNatDroidImpl().getXMLInflateRegistry();
        ClassDescViewBased classDesc = xmlInflateRegistry.getClassDescViewMgr().get(newChildToIn.getName());
        int index = childRef == null ? -1 : getChildIndex(parentNode,childRef);

        View view = createViewObjectAndFillAttributesAndAdd(classDesc, (ViewGroup) parentNode.getView(), newChildToIn, index, xmlInflaterLayout, null);

        newChildToIn.setInserted();
    }

    @Override
    public void insertBefore2(Node parentNode,Node newChild,Node childRef,String newId)
    {
        insertBefore(parentNode, newChild, childRef);
        if (newId != null) addNodeCache2(newId,newChild);
    }

    @Override
    public void insertBefore3(Object[] parentIdObj,Node newChild,Object[] childRefIdObj,String newId)
    {
        Node parentNode = getNode(parentIdObj);
        Node childRef = getNode2(parentNode, childRefIdObj);
        insertBefore2(parentNode, newChild, childRef, newId);
    }

    @Override
    public void appendChild(Node parentNode,Node newChild)
    {
        insertBefore(parentNode, newChild, null);
    }

    @Override
    public void appendChild2(Node parentNode,Node newChild,String newId)
    {
        appendChild(parentNode, newChild);
        if (newId != null) addNodeCache2(newId,newChild);
    }

    @Override
    public void appendChild3(Object[] idObj,Node newChild,String newId)
    {
        Node parentNode = getNode(idObj);
        appendChild2(parentNode, newChild, newId);
    }

    @Override
    public void removeChild(Node child)
    {
        if (child == null) return; // Raro

        removeChild(child.getView());
    }

    private void removeChild(View child)
    {
        if (child == null) return; // Raro

        ViewGroup parentView = (ViewGroup)child.getParent();
        parentView.removeView(child);
    }

    @Override
    public void removeChild2(String id,boolean isText)
    {
        // isText es siempre false
        if (isText) throw new ItsNatDroidException("Unexpected");
        Node child = getNode(new Object[]{id});
        removeChild(child);
    }

    @Override
    public void removeChild3(Object[] parentIdObj,String childRelPath,boolean isText)
    {
        if (isText) throw new ItsNatDroidException("Unexpected");
        Node parentNode = getNode(parentIdObj);
        Node child = getNode2(parentNode, new Object[]{null, childRelPath});
        removeChild(child);
    }

    @Override
    public void removeNodeCache(String[] idList)
    {
        for (String id : idList)
        {
            Node node = nodeCacheById.remove(id);
            if (node == null) continue; // por si acaso, no debería ocurrir
            View view = node.getView();
            ItsNatViewImpl viewData = getItsNatViewImpl(view);
            viewData.setNodeCacheId(null);
        }
    }

    @Override
    public void clearNodeCache()
    {
        nodeCacheById.clear();
    }

    private View getChildNode(int i,Node parentNode)
    {
        View parentView = parentNode.getView();
        if (parentView instanceof ViewGroup)
            return ((ViewGroup)parentView).getChildAt(i);
        return null;
    }

    private int getLenChildNodes(Node node)
    {
        View view = node.getView();
        if (view instanceof ViewGroup)
            return ((ViewGroup)view).getChildCount();
        return 0;
    }

    private void removeAllChild(Node parentNode) // No es público
    {
        while(getLenChildNodes(parentNode) > 0)
        {
            View child = getChildNode(0,parentNode);
            removeChild(child);
        }
    }

    @Override
    public void removeAllChild2(Object[] parentIdObj)
    {
        Node parentNode = getNode(parentIdObj);
        removeAllChild(parentNode);
    }

    @Override
    public Node addNodeCache(Object[] idObj)
    {
        return getNode(idObj);
    }

    @Override
    public void setInnerXML(Node parentNode,String markup)
    {
        appendFragment(parentNode.getView(), markup);
    }

    @Override
    public void setInnerXML2(Object[] idObj,String markup)
    {
        Node parentNode = getNode(idObj);
        setInnerXML(parentNode, markup);
    }

    @Override
    public UserEvent createUserEvent(String name)
    {
        return new UserEventImpl(name);
    }

    @Override
    public void dispatchUserEvent(View currTargetView,UserEvent evt)
    {
        MapList<String,UserEventListener> listenersByName = getUserEventListenersByName(currTargetView);
        if (listenersByName == null) return;

        List<UserEventListener> listeners = listenersByName.get(evt.getName());
        if (listeners == null) return;
        for(UserEventListener listener : listeners)
        {
            UserEventImpl evt2 = (UserEventImpl)listener.createNormalEvent(evt);
            listener.dispatchEvent(evt2);
        }
    }

    @Override
    public void fireUserEvent(View currTargetView,String name)
    {
        UserEvent evt = createUserEvent(name);
        dispatchUserEvent(currTargetView, evt);
    }

    @Override
    public EventStateless createEventStateless()
    {
        return new EventStatelessImpl();
    }

    @Override
    public void dispatchEventStateless(EventStateless evt,int commMode,long timeout)
    {
        EventStatelessImpl evt2 = new EventStatelessImpl(this,(EventStatelessImpl)evt,commMode,timeout);
        evt2.sendEvent();
    }


    @Override
    public void addDroidEL(Object[] idObj,String type,String listenerId,CustomFunction customFunction,boolean useCapture,int commMode,long timeout,int eventGroupCode)
    {
        View currentTarget = getView(idObj);
        if (currentTarget == null /*&& (!type.equals("unload") && !type.equals("load")) */) // En el caso "unload" y "load" se permite que sea nulo el target
            throw new ItsNatDroidException("INTERNAL ERROR");
        ItsNatViewImpl viewData = getItsNatViewImpl(currentTarget);
        DroidEventListener listenerWrapper = new DroidEventListener(this,currentTarget,type,customFunction,listenerId,useCapture,commMode,timeout,eventGroupCode);
        getDroidEventListeners().put(listenerId, listenerWrapper);
        viewData.getEventListeners().add(type,listenerWrapper);

        if (viewData instanceof ItsNatViewNullImpl)
            return; // Nada más que hacer

        ((ItsNatViewNotNullImpl)viewData).registerEventListenerViewAdapter(type);
    }

    @Override
    public void removeDroidEL(String listenerId)
    {
        DroidEventListener listenerWrapper = getDroidEventListeners().remove(listenerId);
        View currentTarget = listenerWrapper.getCurrentTarget(); // En el caso "unload" y "load" puede ser nulo => ¡¡YA NO!!
        ItsNatViewImpl viewData = getItsNatViewImpl(currentTarget);
        viewData.getEventListeners().remove(listenerWrapper.getType(), listenerWrapper);
    }

    @Override
    public void addGlobalEL(GlobalEventListener listener)
    {
        // Por ahora no se usa pero por imitación del Web...
        if (globalEventListeners == null) this.globalEventListeners = new LinkedList<GlobalEventListener>();
        globalEventListeners.add(listener);
    }

    @Override
    public void removeGlobalEL(GlobalEventListener listener) { globalEventListeners.remove(listener); }

    @Override
    public void sendContinueEvent(Object[] idObj,String listenerId,CustomFunction customFunc,int commMode,long timeout)
    {
        Node currTarget = getNode(idObj); // idObj puede ser nulo
        View currTargetView = currTarget != null ? currTarget.getView() : null;
        ContinueEventListener listenerWrapper = new ContinueEventListener(this,currTargetView,customFunc,listenerId,commMode,timeout);
        DOMExtEventImpl evtWrapper = (DOMExtEventImpl)listenerWrapper.createNormalEvent(null);
        listenerWrapper.dispatchEvent(evtWrapper);
    }

    @SuppressWarnings("unchecked")
    private MapList<String,UserEventListener> getUserEventListenersByName(View currTargetView)
    {
        MapList<String, UserEventListener> listenersByName;
        if (currTargetView == null) listenersByName = getUserEventListenersByName();
        else
        {
            ItsNatView itsNatView = getItsNatView(currTargetView);
            listenersByName = (MapList<String, UserEventListener>) itsNatView.getUserData().get(key_itsNatUserListenersByName);
        }
        return listenersByName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addUserEL(Object[] idObj,String name,String listenerId,CustomFunction customFunc,int commMode,long timeout)
    {
        View currTarget = getView(idObj);
        UserEventListener listenerWrapper = new UserEventListener(this,currTarget,name,customFunc,listenerId,commMode,timeout);
        getUserEventListenersById().put(listenerId, listenerWrapper);
        MapList<String,UserEventListener> listenersByName;
        if (currTarget == null) listenersByName = getUserEventListenersByName();
        else
        {
            ItsNatView itsNatView = getItsNatView(currTarget);
            listenersByName = (MapList<String,UserEventListener>)itsNatView.getUserData().get(key_itsNatUserListenersByName);
            if (listenersByName == null)
            {
                listenersByName = new MapListLight<String,UserEventListener>();
                itsNatView.getUserData().set(key_itsNatUserListenersByName,listenersByName);
            }
        }

        listenersByName.add(name, listenerWrapper);
    }

    @Override
    public void removeUserEL(String listenerId)
    {
        UserEventListener listenerWrapper = getUserEventListenersById().remove(listenerId);
        if (listenerWrapper == null) return;

        View currTargetView = listenerWrapper.getCurrentTarget();
        MapList<String,UserEventListener> listenersByName = getUserEventListenersByName(currTargetView);

        listenersByName.remove(listenerWrapper.getName(), listenerWrapper);
    }

    @Override
    public void sendAsyncTaskEvent(Object[] idObj,String listenerId,CustomFunction customFunc,int commMode,long timeout)
    {
        View currTarget = getView(idObj);
        AsyncTaskEventListener listenerWrapper = new AsyncTaskEventListener(this,currTarget,customFunc,listenerId,commMode,timeout);
        DOMExtEventImpl evtWrapper = (DOMExtEventImpl)listenerWrapper.createNormalEvent(null);
        listenerWrapper.dispatchEvent(evtWrapper);
    }

    @Override
    public void addTimerEL(Object[] idObj,String listenerId,CustomFunction customFunc,int commMode,long timeout,long delay)
    {
        View currTarget = getView(idObj);
        final TimerEventListener listenerWrapper = new TimerEventListener(this,currTarget,customFunc,listenerId,commMode,timeout);

        Runnable callback = new Runnable()
        {
            @Override
            public void run()
            {
                // Se ejecutará en el hilo UI
                DOMExtEventImpl evtWrapper = (DOMExtEventImpl) listenerWrapper.createNormalEvent(null);
                try
                {
                    listenerWrapper.dispatchEvent(evtWrapper);
                }
                catch(Exception ex)
                {
                    HttpRequestResult result = (ex instanceof ItsNatDroidServerResponseException) ? ((ItsNatDroidServerResponseException)ex).getHttpRequestResult() : null;

                    ItsNatDroidException exFinal = (ex instanceof ItsNatDroidException) ? (ItsNatDroidException)ex : new ItsNatDroidException(ex);

                    OnEventErrorListener errorListener = getPageImpl().getOnEventErrorListener();
                    if (errorListener != null)
                    {
                        errorListener.onError(exFinal, evtWrapper,result);
                    }
                    else
                    {
                        if (errorMode != ClientErrorMode.NOT_CATCH_ERRORS)
                        {
                            // Error del servidor, lo normal es que haya lanzado una excepción
                            showErrorMessage(true, result,exFinal, errorMode);
                        }
                        else throw exFinal;
                    }
                }
            }
        };
        listenerWrapper.setCallback(callback);
        getHandler().postDelayed(callback, delay);
        getTimerEventListeners().put(listenerId, listenerWrapper);
    }

    @Override
    public void removeTimerEL(String listenerId)
    {
        TimerEventListener listenerWrapper = getTimerEventListeners().remove(listenerId);
        if (listenerWrapper == null) return;
        Runnable callback = listenerWrapper.getCallback();
        getHandler().removeCallbacks(callback);
    }

    @Override
    public void updateTimerEL(String listenerId,long delay)
    {
        TimerEventListener listenerWrapper = getTimerEventListeners().get(listenerId);
        if (listenerWrapper == null) return;
        Runnable callback = listenerWrapper.getCallback();
        getHandler().postDelayed(callback, delay);
    }

    @Override
    public void sendCometTaskEvent(String listenerId,CustomFunction customFunc,int commMode,long timeout)
    {
        CometTaskEventListener listenerWrapper = new CometTaskEventListener(this,listenerId,customFunc,commMode,timeout);
        DOMExtEventImpl evtWrapper = (DOMExtEventImpl)listenerWrapper.createNormalEvent(null);
        listenerWrapper.dispatchEvent(evtWrapper);
    }

    @Override
    public MotionEvent createMotionEvent(String type,float x, float y)
    {
        return DroidMotionEventImpl.createMotionEventNative(type, x, y);
    }

    @Override
    public KeyEvent createKeyEvent(String type,int keyCode)
    {
        return DroidKeyEventImpl.createKeyEventNative(type, keyCode);
    }

    @Override
    public Boolean createFocusEvent(boolean hasFocus)
    {
        return DroidFocusEventImpl.createFocusEventNative(hasFocus);
    }

    @Override
    public CharSequence createTextChangeEvent(CharSequence newText)
    {
        return DroidTextChangeEventImpl.createTextChangeEventNative(newText);
    }

    @Override
    public Object createOtherEvent()
    {
        return DroidOtherEventImpl.createOtherEventNative();
    }

    @Override
    public boolean dispatchEvent(Node node, String type, Object nativeEvt)
    {
        View currTarget = NodeImpl.getView(node);
        return dispatchDroidEvent(currTarget, type, nativeEvt);
    }

    @Override
    public boolean dispatchEvent2(Object[] idObj, String type, Object nativeEvt)
    {
        Node currTarget = getNode(idObj);
        return dispatchEvent(currTarget, type, nativeEvt);
    }

    private boolean dispatchDroidEvent(View target, String type, Object nativeEvt)
    {
        ItsNatViewImpl targetViewData = getItsNatViewImpl(target);
        eventDispatcher.dispatch(targetViewData, type, nativeEvt);
        return false; // No sabemos qué poner
    }

    public void sendUnloadEvent()
    {
        Object nativeEvt = createOtherEvent();

        dispatchDroidEvent(getRootView(), "unload", nativeEvt);

        if (attachUnloadCallback != null)
        {
            attachUnloadCallback.run();
        }
    }

    public void sendLoadEvent()
    {
        Object nativeEvt = createOtherEvent();
        dispatchDroidEvent(getRootView(), "load", nativeEvt);
    }

    @Override
    public boolean dispatchUserEvent2(Object[] idObj,UserEvent evt)
    {
        View currTarget = getView(idObj);
        dispatchUserEvent(currTarget, evt);
        return false; // No sabemos qué poner;
    }

    @Override
    public void initAttachTimerRefresh(final int interval,final int commMode,final long timeout)
    {
        final ItsNatDocItsNatImpl itsNatDoc = this;
        this.attachTimerRefreshCallback = new Runnable()
        {
            @Override
            public void run()
            {

                AttachedClientTimerRefreshEventImpl evtWrapper = new AttachedClientTimerRefreshEventImpl(itsNatDoc,interval,commMode,timeout);
                try
                {
                    evtWrapper.sendEvent();
                }
                catch(Exception ex)
                {
                    OnEventErrorListener errorListener = getPageImpl().getOnEventErrorListener();
                    if (errorListener != null)
                    {
                        HttpRequestResult resultError = (ex instanceof ItsNatDroidServerResponseException) ? ((ItsNatDroidServerResponseException)ex).getHttpRequestResult() : null;
                        errorListener.onError(ex, evtWrapper,resultError);
                    }
                    else
                    {
                        if (ex instanceof ItsNatDroidException) throw (ItsNatDroidException) ex;
                        else throw new ItsNatDroidException(ex);
                    }
                }
            }
        };
        getHandler().postDelayed(attachTimerRefreshCallback, interval);
    }

    @Override
    public void stopAttachTimerRefresh()
    {
        getHandler().removeCallbacks(attachTimerRefreshCallback);
        this.attachTimerRefreshCallback = null;
    }

    @Override
    public void sendAttachCometTaskRefresh(String listenerId,int commMode,long timeout)
    {
        AttachedClientCometTaskRefreshEventImpl evt = new AttachedClientCometTaskRefreshEventImpl(this,listenerId,commMode,timeout);
        evt.sendEvent();
    }

    @Override
    public void addAttachUnloadListener(final int commMode)
    {
        final ItsNatDocItsNatImpl itsNatDoc = this;
        this.attachUnloadCallback = new Runnable()
        {
            @Override
            public void run()
            {
                AttachedClientUnloadEventImpl evt = new AttachedClientUnloadEventImpl(itsNatDoc,commMode,-1);
                evt.sendEvent();
            }
        };
    }


}

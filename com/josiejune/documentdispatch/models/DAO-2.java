package com.josiejune.documentdispatch.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.helper.DAOBase;
import com.josiejune.documentdispatch.handlers.OperatorHandler;

public class DAO extends DAOBase {
	
	//private static final Logger _log = Logger.getLogger(DAO.class.getName());
	
	public DAO () {super();}
	
	static {
		ObjectifyService.register(DDMessage.class);
		ObjectifyService.register(Document.class);
		ObjectifyService.register(StatusCapture.class);
		ObjectifyService.register(DailyStatus.class);
		ObjectifyService.register(Operator.class);
		ObjectifyService.register(DocumentGroup.class);
		ObjectifyService.register(Event.class);
		ObjectifyService.register(Tag.class);
	}
	
	public List<Event> getEventsForDocument (long id) {
		return ofy().query(Event.class)
			.filter("itemId", id)
			.filter("itemType", "Document")
			.order("eventTimestamp").list();
	}
	
	public List<Event> getEventsForMessage (long id) {
		return ofy().query(Event.class)
			.filter("itemId", id)
			.filter("itemType", "Message")
			.order("eventTimestamp").list();
	}
	
	public boolean isUserRegistered (User user) {
//		Map<Key<Operator>, Operator> operatorMap = ofy().get(ofy().query(Operator.class).listKeys());
//		Set<Key<Operator>> keys = operatorMap.keySet();
//		Iterator<Key<Operator>> iterator = keys.iterator();
//		
//		while (iterator.hasNext()) {
//			Operator operator = operatorMap.get(iterator.next());
//			if (operator.getUser().getEmail().equals(user.getEmail())) {
//				return true;
//			}
//		}
		return true;
	}
	
	public DailyStatus getDailyStatus () {
		DailyStatus ds = null;
		try {
			ds = ofy().get(new Key<DailyStatus>(DailyStatus.class, 1));
		}
		catch (NotFoundException nfe) {
			ds = new DailyStatus();
			ds.save();
		}
		return ds;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void tagMessage (long msgId, String tag) {
		DDMessage msg = getMessage(msgId);
		Key key = msg.getKey();
		Tag myTag = new Tag(key, tag);
		ofy().put(myTag);
	}
	
	public List<Tag> getTagsForMessage (DDMessage message) {
		return ofy().query(Tag.class).filter("parent", message).list();
	}
	
	public List<DDMessage> getMessagesForTag (String tag) {
		List<Tag> tags = ofy().query(Tag.class).filter("tag", tag).list();
		List<DDMessage> messages = new ArrayList<DDMessage>();
		for (Tag t : tags) {
			messages.add( ofy().get(t.getParent()) );
		}
		return messages;
	}
	
	public DDMessage getMessage(long id) {
		return ofy().get(new Key<DDMessage>(DDMessage.class, id));
	}
	
	public List<DDMessage> getMessages (Operator operator) {
		return ofy().query(DDMessage.class).filter("parent", operator).list();
	}
	
	public List<DDMessage> getMessageForOrderNum (String orderNum) {
		return ofy().query(DDMessage.class).filter("salesOrderId", orderNum).list();
	}
	
	public boolean isMessageDuplicate (String emailMsgId) {
		if (emailMsgId == null) {
			return false;
		}
		List<DDMessage> messages = ofy().query(DDMessage.class).filter("emailMsgId", emailMsgId).list();
		if (messages.size() > 1) {
			return true;
		}
		return false;
	}
	
	public List<Document> getDocuments (DocumentGroup dg) {
		return ofy().query(Document.class).filter("parent", dg).list();
	}

	public Document getDocWaitingForOCR (Key<Document> key) {
		return ofy().get(key);
	}
	
	public int getNumberReceivedMessagesToday (boolean managed) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.HOUR_OF_DAY, 3);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		List<DDMessage> messages = ofy()
			.query(DDMessage.class)
			.filter("createdDate >=", today.getTime())
			.filter("managed", managed)
			.list();
		int msgCount = 0;
		for (DDMessage msg : messages) {
			msgCount = msgCount + msg.getWorkCount();
		}
		return msgCount;
	}
	
	
	public int getNumberCompletedMessagesToday (boolean managed) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.HOUR_OF_DAY, 3);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		List<DDMessage> messages = ofy()
			.query(DDMessage.class)
			.filter("completedDate >=", today.getTime())
			.filter("managed", managed)
			.list();
		int msgCount = 0;
		for (DDMessage msg : messages) {
			msgCount = msgCount + msg.getWorkCount();
		}
		return msgCount;
	}
	
	public int getNumberOnlineOperators () {
		
		List<Operator> operators = ofy().query(Operator.class)
			.filter("online", true)
			.filter("role", OperatorHandler.PROCESS_PO)
			.list();
		return operators.size();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void movePurchaseOrder (boolean managed, DDMessage po, Operator toOp) {
		int poCount = po.getWorkCount();
		if (managed) {
			toOp.routingManagedOrder(poCount);
		}
		else {
			toOp.routingUnmanagedOrder(poCount);
		}
		toOp.setLastRoutedPOTimestamp(new Date());
		toOp.save();
		po.setParent(new Key(Operator.class, toOp.getKey().getId()));
		po.save();
	}
	
	public void moveDocuments (DocumentGroup in, DocumentGroup out) {
		List<Document> inDocs = in.getDocuments();
		for (Document d : inDocs) {
			d.setParent(out);
			d.save();
		}
		ofy().delete(in);
	}

	public StatusCapture getStatus(String id) {
		if (id == null) {
			StatusCapture sc = new StatusCapture();
			ofy().put(sc);
			return sc;
		}
		StatusCapture found = ofy().find(StatusCapture.class, Long.valueOf(id));
		if (found == null) {
			StatusCapture sc = new StatusCapture();
			ofy().put(sc);
			return sc;
		}
		else {
			return found;
		}
	}
	
	public List<Operator> getSortedOperators () {
		return ofy().query(Operator.class).filter("role", OperatorHandler.PROCESS_PO)
		.order("dwellTimeBetweenOrders")
		.order("lastRoutedPOTimestamp")
		.list();
	}
	
	public List<Operator> getOnlineSortedOperators () {
		return ofy().query(Operator.class).filter("role", OperatorHandler.PROCESS_PO)
		.order("dwellTimeBetweenOrders")
		.order("lastRoutedPOTimestamp")
		.filter("online", true)
		.list();
	}
	
	public List<Operator> getAllOperators () {
		return ofy().query(Operator.class).order("lastRoutedPOTimestamp").list();
	}
	
	public Operator getNullOperator () {
		return getOperatorByName ("No_Operator");
	}
	
	public Operator getOperatorByName (String operatorName) {
		return ofy().query(Operator.class).filter("opName", operatorName).get();
	}
	
	public Operator getOperatorByEmail (String email) {
		return ofy().query(Operator.class).filter("emailAddr", email.toLowerCase()).get();
	}
	
	public List<String> getAllOperatorNames () {
		List<Operator> ops = ofy().query(Operator.class).list();
		List<String> names = new ArrayList<String>();
		for (Operator op : ops) {
			names.add(op.getOpName());
		}
		return names;
	}
	
	public Map<Long, String> getAllOpIDsAndNames() {
		HashMap<Long, String> map = new HashMap<Long, String>();
		List<Operator> ops = ofy().query(Operator.class).list();

		for (Operator op : ops) {
			map.put(op.id, op.getOpName());
		}
		return map;
	}
	
	public void writeEvent (String event, String itemType, long itemId) {
		Event e = new Event (event, itemType, itemId);
		ofy().put(e);
	}
}

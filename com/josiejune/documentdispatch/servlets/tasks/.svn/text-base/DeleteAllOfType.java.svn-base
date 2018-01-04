package com.josiejune.documentdispatch.servlets.tasks;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.api.taskqueue.TaskOptions.Method.POST;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.DeadlineExceededException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.josiejune.documentdispatch.handlers.ExceptionHandler;
import com.josiejune.documentdispatch.models.DAO;

public class DeleteAllOfType extends HttpServlet {

	private static final long serialVersionUID = -8823322548293982679L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		try {
			DAO dao = new DAO();

			Class clazz = Class.forName(request.getParameter("kind"));

//			Objectify ofy = ObjectifyService.begin();
			
			Query<Object> query = dao.ofy().query(clazz);

			QueryResultIterator<Object> iterator = query.iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				Objectify ofy = dao.fact().beginTransaction();
				ofy.delete(o);
				ofy.getTxn().commit();
			}

			//dao.ofy().delete(dao.ofy().query(clazz).list());


		} catch (DeadlineExceededException dee) {
			Queue queue = QueueFactory.getQueue("trashcan");
			queue.add(withUrl("/deletealloftype")
					.param("kind", request.getParameter("kind"))
					.method(POST));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.handleException(e);
		}
	}

}

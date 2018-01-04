package com.josiejune.documentdispatch.servlets.tasks;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.josiejune.documentdispatch.handlers.OperatorHandler;
import com.josiejune.documentdispatch.models.DAO;
import com.josiejune.documentdispatch.models.Operator;

public class LoadDefaultData extends HttpServlet {

	private static final long serialVersionUID = -7330573754253267513L;
	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(LoadDefaultData.class.getName());
	private static final String ADMIN_EMAIL = "max.gravitt@gmail.com";
	private static final String ADMIN_SMS = "9196738460";
	@SuppressWarnings("unused")
	private static final int DEFAULT_MAXORDERS = 5;
	private static final int MAX = 10000000;
//	private static final String ADMIN_SMS = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		DAO dao = new DAO();

		Operator inbound = new Operator(OperatorHandler.INBOUND_OPERATOR, OperatorHandler.ADMIN, ADMIN_EMAIL, ADMIN_EMAIL, ADMIN_SMS, MAX);
		Operator admin = new Operator(OperatorHandler.ADMIN_OPERATOR, OperatorHandler.ADMIN, ADMIN_EMAIL, ADMIN_EMAIL, ADMIN_SMS, MAX);
		Operator archive = new Operator(OperatorHandler.ARCHIVE_OPERATOR, OperatorHandler.ARCHIVE_PO, ADMIN_EMAIL, ADMIN_EMAIL, ADMIN_SMS, MAX);
	
		Operator nullOperator = new Operator("No_Operator", OperatorHandler.PROCESS_PO, ADMIN_EMAIL, ADMIN_EMAIL, ADMIN_SMS, MAX);
		dao.ofy().put(nullOperator);
		
	Operator sarahmilkovich = new Operator("sarah.milkovich", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
		"max.gravitt@gmail.com", "9196739460", 0);
	dao.ofy().put(sarahmilkovich);
//		Operator alexandrawoods = new Operator("alexandra.woods", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(alexandrawoods);
////		Operator kristalombardo = new Operator("krista.lombardo", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
////				"9196739460", 10);
////		dao.ofy().put(kristalombardo);
//		Operator alannacooke = new Operator("alanna.cooke", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(alannacooke);
//		Operator scottpeters = new Operator("scott.peters", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(scottpeters);
////		Operator alibanjak = new Operator("ali.banjak", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
////				"9196739460", 10);
////		dao.ofy().put(alibanjak);
//		Operator kathygeorge = new Operator("kathy.george", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(kathygeorge);
//		Operator krisflynn = new Operator("kristine.flynn", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(krisflynn);
//		Operator adriennegardner = new Operator("adrienne.gardner", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(adriennegardner);
//		Operator adeholloway = new Operator("ade.holloway", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(adeholloway);
//		Operator barbaralingford = new Operator("barbara.lingford", OperatorHandler.PROCESS_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(barbaralingford);
//		Operator markshifflet = new Operator("markshifflet", OperatorHandler.RESOLVE_PO, "max.gravitt@gmail.com", 
//				"9196739460", 10);
//		dao.ofy().put(markshifflet);
//
//
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("AAFES");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Affiliated Foods Midwest");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Alber");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Amcon");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Associated Foods");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Associated Wholesalers");
//		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Babies R Us");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Bear Creek Operations");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Big Lots");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Bi-Mart");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Bloom, Food Lion");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Barnes and Noble");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Waldenbooks");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Bristol Farms");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Jennifer Dumont");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Hartnett");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Carmi");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Casey's General Store");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Cash-Wa");
//	//	OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Certco");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Coastal Pacific");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Core-Mark");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Cracker Barrel");
//		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Destination Maternity");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Diapers");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Quidsi");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Discount Drug");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Eby Brown");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("FAB Wholesale");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Farner Bocken");
//		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Fresh Market");
//		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Frontier");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("General Nutrition");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Granite City Jobbing");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Grocery Supply Company");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Hackney");
//		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Hastings");
//		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("HE Butt");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Henry's");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Sun Harvest");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Mckay");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Hackney");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Hackney");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Hackney");
//		OperatorHandler.getOperatorByName("ade.holloway").addKeyword("Hy-Vee");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Imperial");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("Implus");
//		OperatorHandler.getOperatorByName("ade.holloway").addKeyword("Independent Pharmacy");
//		OperatorHandler.getOperatorByName("ade.holloway").addKeyword("IPC");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Ingles");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("JC Wright");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Kinney");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Klosterman");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("cantwell");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Lamacar");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Liberty");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Marine Corps");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("MCX");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Marmaxx");
//	//	OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Mast General");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Merchants");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("MDV");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Meijer");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("Menard");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Midland Foods");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("MSU Bookstore");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Myers Cox");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("GMS Bellefontaine");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("NF Distributors");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Military, Navy");
//		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Neo International");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Nexcom");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Nexcom");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Office Max");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Overstock");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("Pamida");
//		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Paradies");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Golub");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Publix");
////		OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Publix");
//		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Raley's");
//		OperatorHandler.getOperatorByName("ade.holloway").addKeyword("Regis");
//		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Recreational Equipment");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Ricker");
//	//	OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("Rickys");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Richmond Master");
//		OperatorHandler.getOperatorByName("adrienne.gardner").addKeyword("Ross Stores");
//	//	OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Safeway");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Sandstroms");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Sandstrom's");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Schnucks");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Sheetz");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Spartan");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Spartys, Sparty's");
////		OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Stage");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Starkman");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Stater Brothers");
//	//	OperatorHandler.getOperatorByName("sarah.milkovich").addKeyword("Stein Mart");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Stephenson");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Sullivan Supply");
//		OperatorHandler.getOperatorByName("barbara.lingford").addKeyword("Target.com");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Trend Evolution");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Tricom");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Tri-Mart");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Tripifoods");
////		OperatorHandler.getOperatorByName("krista.lombardo").addKeyword("TS2");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("URM");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Valu Merchandisers");
//		OperatorHandler.getOperatorByName("alanna.cooke").addKeyword("Vitacost");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Vitamin Shoppe");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Wegmans, Wegman's");
//		OperatorHandler.getOperatorByName("scott.peters").addKeyword("Weis");
//	//	OperatorHandler.getOperatorByName("ali.banjak").addKeyword("Whole Foods");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Wholesale Supply Company");
//		OperatorHandler.getOperatorByName("alexandra.woods").addKeyword("Winkler");
//
//
	dao.ofy().put(inbound);
		dao.ofy().put(archive);
		dao.ofy().put(admin);

	}

}



//Operator inbound = new Operator(OperatorHandler.INBOUND_OPERATOR, OperatorHandler.ADMIN, ADMIN_EMAIL, ADMIN_SMS, MAX);
//Operator admin = new Operator(OperatorHandler.ADMIN_OPERATOR, OperatorHandler.ADMIN, ADMIN_EMAIL, ADMIN_SMS, MAX);
//Operator archive = new Operator(OperatorHandler.ARCHIVE_OPERATOR, OperatorHandler.ARCHIVE_PO, ADMIN_EMAIL, ADMIN_SMS, MAX);
//
//Operator sarah_resolution = new Operator ("sarah_resolution", OperatorHandler.RESOLVE_PO, ADMIN_EMAIL, "00", MAX);
//
//Operator bdrenv3george = new Operator("george", OperatorHandler.PROCESS_PO, "bdrenv3george@gmail.com", 
//		"9196739460", 1);
//
//bdrenv3george.addKeyword("AAFES");
//bdrenv3george.addKeyword("Bartel");
//bdrenv3george.addKeyword("Earth Fare");
//bdrenv3george.addKeyword("Hallmark");
//bdrenv3george.addKeyword("Tops");
//bdrenv3george.addKeyword("Vitacost");
//bdrenv3george.addKeyword("Trend Evolution");
//
//Operator bdrenv3ringo = new Operator("ringo", OperatorHandler.PROCESS_PO, "bdrenv3ringo@gmail.com", 
//		"9196739460", 25);
//bdrenv3ringo.addKeyword("Safeway");
//
//Operator bdrenv3john = new Operator("john", OperatorHandler.PROCESS_PO, "bdrenv3john@gmail.com", 
//		"9196739460", 5);
//bdrenv3john.addKeyword("United Supermarkets");
//bdrenv3john.addKeyword("Weis");
//
//
//Operator bdrenv3paul = new Operator("paul", OperatorHandler.PROCESS_PO, "bdrenv3paul@gmail.com", 
//		"9196739460", 25);
//bdrenv3paul.addKeyword("Sprout");
//bdrenv3paul.addKeyword("Wegman");
//
//try {
//	DAO dao = new DAO();
//	dao.ofy().put(bdrenv3paul);
//	dao.ofy().put(bdrenv3john);
//	dao.ofy().put(bdrenv3ringo);
//	dao.ofy().put(bdrenv3george);
//	dao.ofy().put(sarah_resolution);
//	dao.ofy().put(inbound);
//	dao.ofy().put(archive);
//	dao.ofy().put(admin);
//	
//} catch (Exception ex) {
//	ExceptionHandler.handleException(ex);
//}
//}

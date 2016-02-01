package org.quasar.ProjectsWorld.utils;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.cs.Db4oClientServer;

import org.quasar.ProjectsWorld.ProjectsWorldMemory;
import org.quasar.ProjectsWorld.persistenceLayer.Database;
import org.quasar.ProjectsWorld.businessLayer.ModelMusts;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.businessLayer.CalendarDate;
import org.quasar.ProjectsWorld.businessLayer.Company;
import org.quasar.ProjectsWorld.businessLayer.Member;
import org.quasar.ProjectsWorld.businessLayer.Project;
import org.quasar.ProjectsWorld.businessLayer.Qualification;
import org.quasar.ProjectsWorld.businessLayer.Training;
import org.quasar.ProjectsWorld.businessLayer.Worker;


public class ServerActions {

	private static AsyncTask<Void, String, Boolean> send_task;
	private static AsyncTask<Void, String, Boolean> get_task;
	private static ProgressDialog progressDialog;
	
	public static void sendChanges(final Activity activity) {
		send_task = new AsyncTask<Void, String, Boolean>() {
				
			@Override
		    protected void onPreExecute() {
		        progressDialog = new ProgressDialog(activity);
		        progressDialog.setMessage("sending...");
		        progressDialog.setCancelable(false);
		        progressDialog.show();

		    }
			
			@Override
		    protected void onPostExecute(Boolean result) 
		    {
		        progressDialog.cancel();

		    }
		    
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean error = false;
				List<AndroidTransaction> list = new ArrayList<AndroidTransaction>(Transactions.getAllInstances());
//				Log.i("server ttransactions lista", "size - "+ list.size());
//				Log.i("transactions lista",list.toString());
				AndroidTransaction transaction = null;
				
//				TEST
				for(int i = 0;i < list.size();++i){
					Log.i("Transaction", "inside transaction " + list.get(i).toString() + " - start");
					for(Command c : list.get(i).getCommands())
						Log.i("Command:", c.toString());
					Log.i("Transaction", "inside transaction " + list.get(i).toString() + " - end");
				}
				
//				if(transaction != null)
//					Log.i("got transaction", "" + transaction.toString());
				Log.i("Server connection", "vou abrir conecçao");
				try{
					ObjectContainer client = Db4oClientServer.openClient(Database.dbServerConfig(), ServerInfo.HOST, ServerInfo.PORT, ServerInfo.USER,	ServerInfo.PASS);
					Log.i("Server connection", "abri conecçao");
					try {
						ObjectContainer session = client.ext().openSession();
						Log.i("Server connection", "abri sessao");
						try {
							for(int i = 0;i < list.size();++i){
								transaction = list.get(i);
//								Log.i("Transaction", "inside transaction - start");
//								for(Command c : transaction.getCommands())
//									Log.i("Command:", c.toString());
//								Log.i("Transaction", "inside transaction - end");
								for(Command c : transaction.getCommands()){
									//duvida porke a obrigatoriedade do toString(), e a unica maneira aki
//									qual a implicacao de enums no android?? isto so acontece quando limpamos a stack
//									ou seja 
//									se fizermos new receipt e depois back e send funciona assim ou com ==
//									se fizermos new receipt e depois home (limpa a task e fecha db) e send so funciona assim com equals ??
//									c.getType() == CommandType.INSERT -> isto funka no 1º caso no 2º já não :/
//									e é com estas merdas que um gajo perde 10 horas pois este tipo de "erros" nao sao erros mas pronto
									if(c.getTargetLayer().toString().equals(CommandTargetLayer.DATABASE.toString())){
										if(c.getType().toString().equals(CommandType.INSERT.toString())){
//											Log.i("server", "entrei no insert");
//											Log.i("object type", "-" +((ModelMusts) c.getSource()).getType());
											if(Database.get(session, ((ModelMusts) c.getSource()).getType(), c.getoldObjectID()) == null){
	//											condição de ja existencia no server
												Object o = ((ModelMusts) c.getSource()).serverInsert(c.getObject());
												session.store(o);
//												Log.i("object stored", "" + c.getObject().toString());
											}
										}
										if(c.getType().toString().equals(CommandType.UPDATE.toString())){
//											Log.i("server", "update" );
											Object server = Database.get(session, ((ModelMusts) c.getSource()).getType(), c.getoldObjectID());
//											//Object local = Database.get(((ModelMusts) c.getSource()).getType(), ((ModelMusts) c.getSource()).ID(c.getObject()));
											if(server != null){
//												Log.i("server - update", "command source - " + c.getSource().toString());
												((ModelMusts) c.getSource()).serverUpdate(server, c.getObject());
//												Log.i("server - update", "1");
												session.store(server);
//												Log.i("server - update", "2");
	//											Log.i("server - update, insert or delete association", "fiz o store do - " + c.getObject().toString());
											}
										}
										if(c.getType().toString().equals(CommandType.INSERT_ASSOCIATION.toString())){
//											Log.i("server", "insert association" );
											Object server_object = Database.get(session, ((ModelMusts) c.getSource()).getType(), c.getoldObjectID());
											Object server_neibor = Database.get(session, c.getoldNeiborType(),  c.getoldNeiborID());
//											Log.i("server", "server_object ID - " + c.getoldObjectID());
//											if(server_object != null)
//												Log.i("server", "server_object not null - " + c.getoldObjectID());
//											if(server_neibor != null)
//												Log.i("server", "server_neibor not null - " + c.getoldNeiborID());
											if(server_object != null && server_neibor != null){
												((ModelMusts) c.getSource()).serverInsertAssociation(server_object, server_neibor);
//												server = Database.get(((ModelMusts) c.getSource()).getType(), c.getoldObject());
												session.store(server_object);
//												Log.i("server - insert association", "fiz o store do - " + c.getObject().toString());
											}
										}
										if(c.getType().toString().equals(CommandType.DELETE_ASSOCIATION.toString())){
//											Log.i("server", "delete association" );
											Object server_oldObject = Database.get(session, ((ModelMusts) c.getSource()).getType(), c.getoldObjectID());
											Object server_oldNeibor = Database.get(session, c.getoldNeiborType(), c.getoldNeiborID());
											if(server_oldObject != null && server_oldNeibor != null){
												((ModelMusts) c.getSource()).serverDeleteAssociation(server_oldObject, server_oldNeibor);
//												server = Database.get(((ModelMusts) c.getSource()).getType(), c.getoldObject());
												session.store(server_oldObject);
//												Log.i("server - delete association", "fiz o store do - " + server_oldObject.toString());
											}
										}
										if(c.getType().toString().equals(CommandType.DELETE.toString())){
											Object temp = Database.get(session, ((ModelMusts) c.getSource()).getType(), c.getoldObjectID());
//											Log.i("server", "entrei no delete" );
//											if((ModelMusts) c.getSource() == null)
//												Log.i("server", "o getsource esta a devolver null" );
											if(temp != null){
												session.delete(temp);
//												Log.i("server - delete", "fiz o delete do " + c.getoldObjectID());
											}
										}
									}
									
								}

								session.commit();
								Transactions.removeTransaction(transaction);

							}
						} catch (Exception e3) {
							if(transaction != null)
								UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection Warning", "Connection to server was made\nConnection to the database was made\nFailed in setting the transaction: " + transaction.toString() + "\nrolling back changes");
							else
								UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Synchronization", "You have not made any changes\nThere are not any transactions");
							session.rollback();
						} finally {
							Log.i("server actions", "fechei a sessão");
							session.close();
						}
					} catch (Exception e2) {
						error = true;
						UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection Warning", "Connection to server was made\nFailed to connect to the database");
					} finally {
						Log.i("server actions", "fechei a ligacao");
						client.close();
					}
				}catch(Exception e1){
					error = true;
					UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection Warning", "Connection to server failed\ncheck if you are connected to the internet");
				}
				if(!error)
					UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "objects sent", "objects sent ");
				return null;
			}
		}.execute();
		
	}

	public static void getChanges(final Activity activity) {
		get_task = new AsyncTask<Void, String, Boolean>() {
		
			@Override
		    protected void onPreExecute() {
		        progressDialog = new ProgressDialog(activity);
		        progressDialog.setMessage("Synchronizing...");
		        progressDialog.setCancelable(false);
		        progressDialog.show();

		    }
			
			@Override
		    protected void onPostExecute(Boolean result) 
		    {
		        progressDialog.cancel();

		    }
		    
			@Override
			protected Boolean doInBackground(Void... params) {
//				ObjectContainer client = Db4oClientServer.openClient(Database.dbServerConfig(), ServerInfo.HOST, ServerInfo.PORT, ServerInfo.USER,	ServerInfo.PASS);
//				ObjectSet<Object> allObjects = client.query(Object.class);
//				for (Object objectToUpdate : allObjects) {
//				   Log.i("objects in server", "" + objectToUpdate.toString());
//				}
//				Log.i("pedir ver objectos todos", "" + "ja pedi ao server");
				boolean error = false;
				ObjectContainer client = null;
				ObjectContainer session = null;
				try{
					client = Db4oClientServer.openClient(ServerInfo.HOST, ServerInfo.PORT, ServerInfo.USER,	ServerInfo.PASS);
				}catch (Exception e1){
					error = true;
					UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection Warning", "Connection to server failed\ncheck if you are connected to the internet");
				}
				if(client != null){
					try {
						session = client.ext().openSession();
					} catch (Exception e2) {
						error = true;
						UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection Warning", "Connection to server sucess\nproblem getting session");
					}
					
					if(session != null){
						try {
							Database.close();
							Database.DeleteDB();
							ObjectContainer local = Database.OpenDB();
							List<Object> allObjects = new ArrayList<Object>();
							
							allObjects.addAll(session.query(CalendarDate.class));
							allObjects.addAll(session.query(Company.class));
							allObjects.addAll(session.query(Member.class));
							allObjects.addAll(session.query(Project.class));
							allObjects.addAll(session.query(Qualification.class));
							allObjects.addAll(session.query(Training.class));
							allObjects.addAll(session.query(Worker.class));
							for (Object objectToUpdate : allObjects) {
							    local.store(objectToUpdate);
							}
							local.commit();
						} catch (Exception e3) {
							error = true;
							UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Connection  problem", "problem during the storage\nrolling back changes");
						} finally {
							session.close();
							client.close();
						}
					}
				}
				if(!error)
					UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Synchronization sucess", "new DB is ready for use");
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static void synchronize(final Activity activity) {
//		sendChanges();
//		if(send_task.getStatus() == AsyncTask.Status.FINISHED)
			getChanges(activity);// deve ter ordem com tasks não pode ser so assim
	}
}

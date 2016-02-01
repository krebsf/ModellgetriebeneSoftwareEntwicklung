package org.quasar.IceCream2.utils;

import java.util.List;

import android.app.Activity;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import org.quasar.IceCream2.persistenceLayer.Database;
import org.quasar.IceCream2.businessLayer.ModelMusts;

public class Transactions {
	
	public Transactions() {

	}
	
	private transient static AndroidTransaction transaction;
	private transient static boolean TransactionCanceled = false;
	private transient static boolean Opened = false;
	private transient static ObjectContainer session;
	
	
	public synchronized static boolean StartTransaction(){
		if(!Opened){//can enter/can start a transaction
			transaction = new AndroidTransaction();
			session = Database.OpenDB();
			Opened = true;
			TransactionCanceled = false;
			return Opened;
		}else
			return !Opened;
	}
	
	public synchronized static boolean StopTransaction(){
		if(Opened && !isCanceled()){
			try{
				session.store(transaction);
				session.commit();
				
			}catch(Exception e){
				CancelTransaction("Finishing transaction error", "error in storing transaction");
				Opened = false;
				return false;
			}
			
			for(Command c : transaction.getCommands())
				if(c.getTargetLayer().toString().equals(CommandTargetLayer.VIEW.toString()))
					((ModelMusts) c.getSource()).notifyObjectListener(c.getSource(), c.getType(), c.getoldObjectID(), c.getoldObject(), c.getObject(), c.getoldNeiborID(), c.getOldNeibor(), c.getNeibor());
			
			Opened = false;
			return true;
			
		}else
			Opened = false;
			return false;
	}
	
	
	public synchronized static void CancelTransaction(String ErrorTittle, String ErroMessage){
		if(Opened){
			Opened = false;
			TransactionCanceled = true;
			transaction.setErrorTittle(ErrorTittle);
			transaction.addErrorMessage(ErroMessage);
			session.rollback();
		}
	}
	
	public synchronized static boolean isCanceled(){
		return TransactionCanceled;
	}
	
	public synchronized static void ShowErrorMessage(Activity activity){
		UtilNavigate.showWarning(activity,transaction.getErrorTittle() , transaction.getErrorMessage().toString());
	}
	
	public synchronized static void AddCommand(Command command){
		if(Opened){
			transaction.getCommands().add(command);
		}
	}
	
	public synchronized static ObjectSet<AndroidTransaction> getAllInstances(){
		return Database.allInstancesOrdered(AndroidTransaction.class);
	}
	
	public synchronized static void removeTransaction(AndroidTransaction x){
		Database.OpenDB().delete(x);
		Database.OpenDB().commit();
	}
	
	public synchronized List<Command> getTransactionCommands(AndroidTransaction transaction){
		return transaction.getCommands();
	}
	
	public synchronized static ObjectContainer getSession(){
		return session;
	}
	
	public synchronized static void addSpecialCommand(Command command){
		AndroidTransaction transaction = new AndroidTransaction();
		transaction.getCommands().add(command);
		
		ObjectContainer sess = Database.OpenDB();
		sess.store(transaction);
		sess.commit();
		
	}
	
}

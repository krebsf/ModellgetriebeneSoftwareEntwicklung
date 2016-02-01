/**********************************************************************
* Filename: Worker.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Worker;

import java.io.Serializable;
import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.ProjectsWorldMemory;
import org.quasar.ProjectsWorld.utils.ListViewHolder;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.businessLayer.Worker;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class WorkerListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.worker_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Worker_nickname = R.id.worker_list_nickname_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Worker_nicknameView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Worker_nicknameView = (TextView) convertView.findViewById(Worker_nickname);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Worker_nicknameView != null)
				holder.Worker_nicknameView.setText("" + ((Worker) object).nickname());
			if(((Worker) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(WorkerListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
					if(((Worker) object).qualifications().size() < 1)
						message += "This Worker must have at least 1 qualifications\n";
					if(convertView.isActivated())
						UtilNavigate.showWarning(ProjectsWorldMemory.getActiveActivity(), "Constraints not met", message);
				}
			});
		}
		return convertView;
	}
	
	@Override
	public View setNewViewHolder(Activity context, View convertView)
	{
		convertView = context.getLayoutInflater().inflate(layout, null);
		holder = new ViewHolder(convertView);
		convertView.setTag(holder);
		return convertView;
	}
	
	@Override
	public int getViewHolder()
	{
		return this.layout;
	}
}

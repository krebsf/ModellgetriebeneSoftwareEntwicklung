/**********************************************************************
* Filename: Training.java
* Created: 2016/01/14
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Training;

import java.io.Serializable;
import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.ProjectsWorldMemory;
import org.quasar.ProjectsWorld.utils.ListViewHolder;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.businessLayer.Training;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class TrainingListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.training_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Project_name = R.id.project_list_name_value;
	private final int Project_size = R.id.project_list_size_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Project_nameView;
		public TextView Project_sizeView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Project_nameView = (TextView) convertView.findViewById(Project_name);
			this.Project_sizeView = (TextView) convertView.findViewById(Project_size);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Project_nameView != null)
				holder.Project_nameView.setText("" + ((Training) object).name());
			if(holder.Project_sizeView != null)
				holder.Project_sizeView.setText("" + ((Training) object).size());
			if(((Training) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(TrainingListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
					if(((Training) object).members().size() < 1)
						message += "This Training must have at least 1 members\n";
					if(((Training) object).requirements().size() < 1)
						message += "This Training must have at least 1 requirements\n";
					if(((Training) object).company() == null)
						message += "This Training must have at least 1 company\n";
					if(((Training) object).trained().size() < 1)
						message += "This Training must have at least 1 trained\n";
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

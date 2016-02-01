/**********************************************************************
* Filename: Qualification.java
* Created: 2016/01/14
* @author Lu�s Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.ProjectsWorld.presentationLayer.Qualification;

import java.io.Serializable;
import org.quasar.ProjectsWorld.R;
import org.quasar.ProjectsWorld.utils.Transactions;
import org.quasar.ProjectsWorld.utils.Command;
import org.quasar.ProjectsWorld.utils.CommandType;
import org.quasar.ProjectsWorld.utils.CommandTargetLayer;
import org.quasar.ProjectsWorld.ProjectsWorldMemory;
import org.quasar.ProjectsWorld.utils.ListViewHolder;
import org.quasar.ProjectsWorld.utils.UtilNavigate;
import org.quasar.ProjectsWorld.businessLayer.Qualification;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class QualificationListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.qualification_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Qualification_description = R.id.qualification_list_description_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Qualification_descriptionView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Qualification_descriptionView = (TextView) convertView.findViewById(Qualification_description);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Qualification_descriptionView != null)
				holder.Qualification_descriptionView.setText("" + ((Qualification) object).description());
			if(((Qualification) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(QualificationListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
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

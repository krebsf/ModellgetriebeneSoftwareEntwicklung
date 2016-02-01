/**********************************************************************
* Filename: Station.java
* Created: 2016/01/30
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream.presentationLayer.Station;

import java.io.Serializable;
import org.quasar.IceCream.R;
import org.quasar.IceCream.utils.Transactions;
import org.quasar.IceCream.utils.Command;
import org.quasar.IceCream.utils.CommandType;
import org.quasar.IceCream.utils.CommandTargetLayer;
import org.quasar.IceCream.IceCreamMemory;
import org.quasar.IceCream.utils.ListViewHolder;
import org.quasar.IceCream.utils.UtilNavigate;
import org.quasar.IceCream.businessLayer.Station;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class StationListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.station_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Station_name = R.id.station_list_name_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Station_nameView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Station_nameView = (TextView) convertView.findViewById(Station_name);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Station_nameView != null)
				holder.Station_nameView.setText("" + ((Station) object).name());
			if(((Station) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(StationListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
					message += "This Station must have at least 0 place\n";
					if(convertView.isActivated())
						UtilNavigate.showWarning(IceCreamMemory.getActiveActivity(), "Constraints not met", message);
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

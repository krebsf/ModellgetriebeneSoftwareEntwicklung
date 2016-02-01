/**********************************************************************
* Filename: Entry.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream2.presentationLayer.Entry;

import java.io.Serializable;
import org.quasar.IceCream2.R;
import org.quasar.IceCream2.utils.Transactions;
import org.quasar.IceCream2.utils.Command;
import org.quasar.IceCream2.utils.CommandType;
import org.quasar.IceCream2.utils.CommandTargetLayer;
import org.quasar.IceCream2.IceCreamMemory;
import org.quasar.IceCream2.utils.ListViewHolder;
import org.quasar.IceCream2.utils.UtilNavigate;
import org.quasar.IceCream2.businessLayer.Entry;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class EntryListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.entry_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Entry_date = R.id.calendardate_list_calendardate_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Entry_dateView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Entry_dateView = (TextView) convertView.findViewById(Entry_date);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Entry_dateView != null)
				holder.Entry_dateView.setText("" + ((Entry) object).date().year() + "/" + (((Entry) object).date().month() + 1) + "/" + ((Entry) object).date().day());
			if(((Entry) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(EntryListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
					if(((Entry) object).station() == null)
						message += "This Entry must have at least 1 station\n";
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

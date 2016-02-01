/**********************************************************************
* Filename: Address.java
* Created: 2016/01/31
* @author Luís Pires da Silva and Fernando Brito e Abreu
**********************************************************************/
package org.quasar.IceCream3.presentationLayer.Address;

import java.io.Serializable;
import org.quasar.IceCream3.R;
import org.quasar.IceCream3.utils.Transactions;
import org.quasar.IceCream3.utils.Command;
import org.quasar.IceCream3.utils.CommandType;
import org.quasar.IceCream3.utils.CommandTargetLayer;
import org.quasar.IceCream3.IceCreamMemory;
import org.quasar.IceCream3.utils.ListViewHolder;
import org.quasar.IceCream3.utils.UtilNavigate;
import org.quasar.IceCream3.businessLayer.Address;

import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class AddressListViewHolder implements ListViewHolder, Serializable{
	private transient ViewHolder holder;
	private final int layout = R.layout.address_view_list;
	private final int contractError = R.id.default_list_invalid;
	private final int Address_street = R.id.address_list_street_value;
	private final int Address_postCode = R.id.address_list_postcode_value;
	
	private class ViewHolder
	{
		public ImageView errorIcon;
		public TextView Address_streetView;
		public TextView Address_postCodeView;
		
		public ViewHolder(View convertView)
		{
			this.errorIcon = (ImageView) convertView.findViewById(contractError);
			this.Address_streetView = (TextView) convertView.findViewById(Address_street);
			this.Address_postCodeView = (TextView) convertView.findViewById(Address_postCode);
		}
	}
	
	@Override
	public View setViewHolderContent(final View convertView, final Object object)
	{
		if(object != null)
		{
			holder = (ViewHolder) convertView.getTag();
			if(holder.Address_streetView != null)
				holder.Address_streetView.setText("" + ((Address) object).street());
			if(holder.Address_postCodeView != null)
				holder.Address_postCodeView.setText("" + ((Address) object).postCode());
			if(((Address) object).isAssociationRestrictionsValid())
				holder.errorIcon.setVisibility(View.INVISIBLE);
			else
				holder.errorIcon.setVisibility(View.VISIBLE);
			holder.errorIcon.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Transactions.addSpecialCommand(new Command(AddressListViewHolder.class, CommandType.READ, CommandTargetLayer.VIEW, 0, null, null, null, 0, null, null));
					String message = "";
					message += "This Address must have at least 1 station\n";
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

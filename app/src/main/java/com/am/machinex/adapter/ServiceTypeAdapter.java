package com.am.machinex.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.am.machinex.R;
import com.am.machinex.models.ServiceType;

import java.util.ArrayList;

public class ServiceTypeAdapter extends RecyclerView.Adapter<ServiceTypeAdapter.ViewHolder> {

    public ArrayList<ServiceType> arrayList;
    boolean isSubmitClick = false;
    private Context context;
    ServiceType serviceType = new ServiceType();

    public ServiceTypeAdapter(Context context, ArrayList<ServiceType> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.listview_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceTypeAdapter.ViewHolder holder, int position) {
        ServiceType data = arrayList.get(holder.getAdapterPosition());
        holder.rb_checked.setChecked(data.isSelectedcheck());
        holder.rb_checked.setTag(data);

        holder.rb_unchecked.setChecked(data.isSelectedunckecked());
        holder.rb_unchecked.setTag(data);

        holder.tv_service_name.setText(data.Checktype);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_service_name;
        RadioButton rb_checked;
        RadioButton rb_unchecked;
        RadioGroup radioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            rb_checked = itemView.findViewById(R.id.radiochecked);
            rb_unchecked = itemView.findViewById(R.id.radiounchecked);
            tv_service_name = itemView.findViewById(R.id.code);
            radioGroup = itemView.findViewById(R.id.radio);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radiochecked:
                            // do operations specific to this selection
                            Log.e("hello" + checkedId,"radio check"+rb_checked.isChecked());
                            serviceType = (ServiceType) rb_checked.getTag();
                            serviceType.setSelectedcheck(rb_checked.isChecked());
                            break;
                        case R.id.radiounchecked:
                            // do operations specific to this selection
                            Log.e("hello"+checkedId,"radio uncheck"+rb_unchecked.isChecked());
                            serviceType = (ServiceType) rb_unchecked.getTag();
                            serviceType.setSelectedunckecked(rb_unchecked.isChecked());
                            break;
                    }
                }


            });
        }
    }

    public void setSubmitClick(boolean isTrue) {
        this.isSubmitClick = isTrue;
    }

}

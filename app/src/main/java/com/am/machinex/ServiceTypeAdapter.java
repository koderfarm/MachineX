package com.am.machinex;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ServiceTypeAdapter extends RecyclerView.Adapter<ServiceTypeAdapter.ViewHolder> {

    ArrayList<ServiceType> arrayList;
    boolean isSubmitClick = false;
    private Context context;

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
        if (holder.edit_partname.getText().toString() == null) {
            if (isSubmitClick) {
                holder.textInput.setError("please enter a valid input");
            }
            data.setTextEmpty(true);
        } else {
            data.setTextEmpty(false);
            //do something else
        }
        holder.tv_service_name.setText(data.getChecktype());
        holder.checkBox.setChecked(data.isSelected());
        holder.checkBox.setTag(data);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textInput;
        EditText edit_partname;
        TextView tv_service_name;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            edit_partname = itemView.findViewById(R.id.edit_partname);
            textInput = itemView.findViewById(R.id.tilTest);
            checkBox = itemView.findViewById(R.id.servicetypecb);
            tv_service_name = itemView.findViewById(R.id.code);

            edit_partname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    arrayList.get(getAdapterPosition()).setInput(editable.toString());
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ServiceType serviceType = (ServiceType) cb.getTag();
                    serviceType.setSelected(cb.isChecked());
                    if (!cb.isChecked()) {
                        edit_partname.setVisibility(View.GONE);
                    } else {
                        if (serviceType.getInput().equals("Y")) {
                            edit_partname.setVisibility(View.VISIBLE);
                        }
                    }


                }
            });

        }
    }

    public void setSubmitClick(boolean isTrue) {
        this.isSubmitClick = isTrue;
    }

}
/*
public class ServiceTypeAdapter extends ArrayAdapter<ServiceType> {

    //storing all the names in the list
    public List<ServiceType> serviceTypeList;

    //context object
    private Context context;

    //constructor
    public ServiceTypeAdapter(Context context, int resource, List<ServiceType> names) {
        super(context, resource, names);
        this.context = context;
        this.serviceTypeList = names;
    }

    private class ViewHolder {
        TextView tv_service_name;
        CheckBox checkBox;
        EditText edit_partname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.listview_checkbox, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.servicetypecb);
            holder.tv_service_name = (TextView) convertView.findViewById(R.id.code);
            holder.edit_partname = (EditText) convertView.findViewById(R.id.edit_partname);
            convertView.setTag(holder);

            ViewHolder finalHolder = holder;
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    ServiceType serviceType = (ServiceType) cb.getTag();
                    serviceType.setSelected(cb.isChecked());
                    if (!cb.isChecked()){
                        finalHolder.edit_partname.setVisibility(View.GONE);
                    }else {
                        if (serviceType.getInput().equals("Y")){
                            finalHolder.edit_partname.setVisibility(View.VISIBLE);
                        }
                    }


                }
            });
            holder.edit_partname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    serviceTypeList.get(getAdapterPosition()).setUserName(charSequence.toString());


                }
                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ServiceType country = serviceTypeList.get(position);
        holder.tv_service_name.setText(country.getChecktype());
//        holder.edit_partname.setText(country.setInput(holder.edit_partname.getText().toString().trim()));
//        holder.edit_partname.setText(holder.edit_partname.getText().toString().trim());
        holder.checkBox.setChecked(country.isSelected());
        holder.checkBox.setTag(country);

        return convertView;
    }
}




*/

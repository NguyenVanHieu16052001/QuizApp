package com.ptithcm.quizapp.adapter;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.quizapp.DetailQuestion;
import com.ptithcm.quizapp.R;
import com.ptithcm.quizapp.database.DBHelper;
import com.ptithcm.quizapp.model.Question;

import java.util.ArrayList;
import java.util.Objects;

public class CustomAdapterQuestion extends RecyclerView.Adapter<CustomAdapterQuestion.QSViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Question> data_filter;
    private ArrayList<Question> data_old;
    private ArrayList<Question> qs_Selected = new ArrayList<>();
    private boolean isEnable = false;

    AlertDialog.Builder builder;
    TextView textTitle, textMessage;
    Button buttonYes, buttonNo, buttonAction;
    ImageView imageIcon;

    public CustomAdapterQuestion(@NonNull Context context, @NonNull ArrayList<Question> data) {
        this.context = context;
        this.data_filter = data;
        this.data_old = data;
        builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
    }


    @NonNull
    @Override
    public QSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QSViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QSViewHolder holder, int position) {
        Question qs = data_filter.get(position);
        holder.tvNumbId.setText("#" + qs.getQuestionID());
        String txt = qs.getQuestionContent();
        if (txt.length() > 20) {
            holder.tvTitle.setText(txt.substring(0, 20) + "...");
        } else {
            holder.tvTitle.setText(qs.getQuestionContent());
        }
        holder.tvLever.setText("Level " + qs.getQuestionLevel());
        holder.layoutItemQS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailQuestion.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("questionID", qs.getQuestionID());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }


        });
        holder.tvNumbId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (!isEnable) {
                        ActionMode.Callback callback = new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.menu_qs_selected, menu);
                                
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                isEnable = true;
                                clickItem(holder);
                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.btnDelete_tb:
                                        if (qs_Selected.isEmpty()) {
                                            mode.finish();
                                            break;
                                        }
                                        showWarningDialog(context.getResources().getString(R.string.delete));

                                        mode.finish();
                                        break;
                                }
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                isEnable = false;
                                qs_Selected.clear();
                                notifyDataSetChanged();
                            }
                        };
                        ((AppCompatActivity) v.getContext()).startActionMode(callback);
                    } else {
                        clickItem(holder);
                    }
                }
            }
        });
        if (qs_Selected.isEmpty()) {
            holder.isSelected = false;
            holder.layoutItemQS.setBackground(context.getDrawable(R.drawable.custom_background_item_qs));
        }
    }

    private void clickItem(QSViewHolder holder) {
        Question qs = data_old.get(holder.getAdapterPosition());
        if (!holder.isSelected) {
            holder.layoutItemQS.setBackground(context.getDrawable(R.drawable.custom_background_item_qs_selected));
            holder.isSelected = true;
            qs_Selected.add((qs));
        } else {
            holder.layoutItemQS.setBackground(context.getDrawable(R.drawable.custom_background_item_qs));

            holder.isSelected = false;
            qs_Selected.remove((qs));
        }
    }

    @Override
    public int getItemCount() {
        return data_filter.size();
    }

    public class QSViewHolder extends RecyclerView.ViewHolder {

        TextView tvNumbId, tvTitle, tvLever;
        LinearLayout layoutItemQS;
        boolean isSelected;

        public QSViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumbId = itemView.findViewById(R.id.tvNumbId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLever = itemView.findViewById(R.id.tvLever);
            layoutItemQS = itemView.findViewById(R.id.layoutItemQS);
            isSelected = false;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString().toLowerCase().trim();
                if (strSearch.isEmpty()) {
                    data_filter = data_old;
                } else {
                    data_filter = new ArrayList<Question>();
                    for (Question qs : data_old) {
                        if (qs.getQuestionContent().toLowerCase().contains(strSearch)) {
                            data_filter.add(qs);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = data_filter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data_filter = (ArrayList<Question>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private void setControl(View view) {
        textTitle = view.findViewById(R.id.textTitle);
        textMessage = view.findViewById(R.id.textMessage);
        buttonYes = view.findViewById(R.id.buttonYes);
        buttonNo = view.findViewById(R.id.buttonNo);
        buttonAction = view.findViewById(R.id.buttonAction);
        imageIcon = view.findViewById(R.id.imageIcon);
    }

    private ArrayList<String> getListIDSelected() {
        ArrayList<String> ids = new ArrayList<>();
        for (Question qs : qs_Selected) {
            ids.add(qs.getQuestionID());
        }
        return ids;
    }

    public void showSuccessDialog(String mess) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_success_dailog, null);
        setControl(view);
        builder.setView(view);

        textTitle.setText(context.getResources().getString(R.string.success));
        textMessage.setText(mess);
        buttonAction.setText(context.getResources().getString(R.string.okay));
        imageIcon.setImageResource(R.drawable.ic_baseline_done_24);
        AlertDialog alertDialog;
        alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void showWarningDialog(String mess) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_warning_dailog, null);
        setControl(view);
        builder.setView(view);
        textTitle.setText(context.getResources().getString(R.string.warning));
        textMessage.setText(mess);
        buttonYes.setText(context.getResources().getString(R.string.yes));
        buttonNo.setText(context.getResources().getString(R.string.no));
        imageIcon.setImageResource(R.drawable.ic_baseline_warning_24);

        ArrayList<String> ids = new ArrayList<>();
        ArrayList<Question> qs_selected_old = new ArrayList<>();
        qs_selected_old.addAll(qs_Selected);
        ids.addAll(getListIDSelected());

        AlertDialog alertDialog;
        alertDialog = builder.create();
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(mess, context.getResources().getString(R.string.exit))) {
                    System.exit(1);
                } else {
                    DBHelper dbHelper = new DBHelper(context);
                    if (dbHelper.deleteQuestions(ids)) {

                        data_filter.removeAll(qs_selected_old);
                        data_old.removeAll(qs_selected_old);
                        showSuccessDialog("Deleted");
                    } else {
                        showErrorDialog("Can't delete");
                    }
                }
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    public void showErrorDialog(String mess) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_error_dailog, null);
        builder.setView(view);
        setControl(view);
        textTitle.setText(context.getResources().getString(R.string.error));
        textMessage.setText(mess);
        buttonAction.setText(context.getResources().getString(R.string.okay));
        imageIcon.setImageResource(R.drawable.ic_baseline_error_24);

        AlertDialog alertDialog;
        alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}

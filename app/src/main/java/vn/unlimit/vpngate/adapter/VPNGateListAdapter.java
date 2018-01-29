package vn.unlimit.vpngate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.unlimit.vpngate.GlideApp;
import vn.unlimit.vpngate.R;
import vn.unlimit.vpngate.models.VPNGateConnection;
import vn.unlimit.vpngate.models.VPNGateConnectionList;

/**
 * Created by hoangnd on 1/29/2018.
 */

public class VPNGateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private VPNGateConnectionList _list;

    public VPNGateListAdapter(Context context) {
        mContext = context;
        _list = new VPNGateConnectionList();
    }

    public void initialize(VPNGateConnectionList vpnGateConnectionList) {
        try {
            _list.clear();
            if (vpnGateConnectionList != null) {
                _list.addAll(vpnGateConnectionList);
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        onItemClickListener = _onItemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((VHTypeVPN) viewHolder).bindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VHTypeVPN(parent);
    }

    private class VHTypeVPN extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgFlag;
        TextView txtCountry;
        TextView txtIp;
        TextView txtHostname;
        TextView txtUptime;
        TextView txtSpeed;
        TextView txtPing;
        TextView txtSession;
        TextView txtOwner;

        VHTypeVPN(View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.img_flag);
            txtCountry = itemView.findViewById(R.id.txt_country);
            txtIp = itemView.findViewById(R.id.txt_ip);
            txtHostname = itemView.findViewById(R.id.txt_hostname);
            txtUptime = itemView.findViewById(R.id.txt_uptime);
            txtSpeed = itemView.findViewById(R.id.txt_speed);
            txtPing = itemView.findViewById(R.id.txt_ping);
            txtSession = itemView.findViewById(R.id.txt_session);
            txtOwner = itemView.findViewById(R.id.txt_owner);
            itemView.setOnClickListener(this);
        }

        void bindViewHolder(int position) {
            String baseImgUrl = "http://www.vpngate.net/images/flags/";
            try {
                VPNGateConnection vpnGateConnection = _list.get(position);
                GlideApp.with(mContext)
                        .load(baseImgUrl + vpnGateConnection.getCountryShort() + ".png")
                        .placeholder(R.color.colorOverlay)
                        .error(R.color.colorOverlay)
                        .into(imgFlag);
                txtCountry.setText(vpnGateConnection.getCountryLong());
                txtIp.setText(vpnGateConnection.getIp());
                txtHostname.setText(vpnGateConnection.getHostName());
                txtUptime.setText(vpnGateConnection.getUptime());
                txtSpeed.setText(vpnGateConnection.getSpeed());
                txtPing.setText(vpnGateConnection.getPing());
                txtSession.setText(vpnGateConnection.getNumVpnSession());
                txtOwner.setText(vpnGateConnection.getOperator());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            try {
                if (onItemClickListener != null) {
                    VPNGateConnection item = _list.get(getAdapterPosition());
                    onItemClickListener.onItemClick(item, getAdapterPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

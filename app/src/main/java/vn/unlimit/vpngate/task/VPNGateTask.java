package vn.unlimit.vpngate.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.unlimit.vpngate.models.VPNGateConnection;
import vn.unlimit.vpngate.models.VPNGateConnectionList;
import vn.unlimit.vpngate.request.RequestListener;

/**
 * Created by dongh on 14/01/2018.
 */

public class VPNGateTask extends AsyncTask<Void, Void, VPNGateConnectionList> {
    private static String VPN_GATE_API_URL = "http://www.vpngate.net/api/iphone/";
    private RequestListener requestListener;

    @Override
    protected VPNGateConnectionList doInBackground(Void... voids) {
        VPNGateConnectionList vpnGateConnectionList = new VPNGateConnectionList();
        HttpURLConnection connection = null;
        InputStreamReader inputStreamReader = null;
        try {
            URL url = new URL(VPN_GATE_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();
            vpnGateConnectionList = getConnectionList(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect();
                inputStreamReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vpnGateConnectionList;
    }

    // convert InputStream to VPNGateConnectionList
    private VPNGateConnectionList getConnectionList(InputStream is) {
        VPNGateConnectionList vpnGateConnectionList = new VPNGateConnectionList();
        BufferedReader br = null;

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                if (line.indexOf("*") != 0 && line.indexOf("#") != 0) {
                    VPNGateConnection vpnGateConnection = VPNGateConnection.fromCsv(line);
                    if (vpnGateConnection != null) {
                        vpnGateConnectionList.add(vpnGateConnection);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return vpnGateConnectionList;

    }

    @Override
    protected void onPostExecute(VPNGateConnectionList vpnGateConnections) {
        if (!isCancelled() && requestListener != null) {
            if (vpnGateConnections.size() > 0) {
                requestListener.onSuccess(vpnGateConnections);
            } else {
                requestListener.onError("unknown");
            }
        }
        super.onPostExecute(vpnGateConnections);
    }

    public void setRequestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    public void stop() {
        this.cancel(true);
        requestListener = null;
    }
}
package com.example.grabadorvoz;
import android.content.Context;
import android.content.SharedPreferences;

public class GpsChecker {

    private static final String PREF_NAME = "MyPref";
    private static final String KEY_LATITUD = "Latitud";
    private static final String KEY_LONGITUD = "Longitud";
    private static final String KEY_NOTIFICATION_SENT = "NotificationSent";
    private static final String NULL = "null";
    private static final Integer RANGO = 100;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public GpsChecker(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setUbication(String machineName, String latitud, String longitud){
        editor.putString(machineName + KEY_LATITUD, latitud);
        editor.putString(machineName + KEY_LONGITUD, longitud);
        editor.apply();
    }

    public Boolean notificationChanged(String machineName, String latitud, String longitud){
        String oldLATITUD = preferences.getString(machineName + KEY_LATITUD, null);
        String oldLONGITUD = preferences.getString(machineName + KEY_LONGITUD, null);
        if (oldLATITUD == null && oldLONGITUD == null){
            return true;
        } else {
            try {
                /*oldLATITUD = data(oldLATITUD);
                oldLONGITUD = data(oldLONGITUD);
                latitud = data(latitud);
                longitud = data(longitud);

                int newlongitud = Integer.parseInt(longitud);
                int newlatitud = Integer.parseInt(latitud);

                int oldLatitudInt = Integer.parseInt(oldLATITUD) + 10;
                int oldLongitudInt = Integer.parseInt(oldLONGITUD) + 10;

                if (newlatitud > oldLatitudInt || newlongitud > oldLongitudInt) {
                    return true;
                } else if (newlatitud> oldLatitudInt - 20  || newlongitud > oldLongitudInt- 20){
                    return true;
                }else {
                    oldLATITUD = otherData(oldLATITUD);
                    oldLONGITUD = otherData(oldLONGITUD);
                    latitud = otherData(latitud);
                    longitud = otherData(longitud);
                    if (oldLATITUD.equals(latitud) || oldLONGITUD.equals(longitud)) {
                        return false;
                    } else {
                        return false;
                    }
                }*/
                float oldLatitud = Float.parseFloat(oldLATITUD);
                float oldLongitud = Float.parseFloat(oldLONGITUD);
                float newLatitud = Float.parseFloat(latitud);
                float newLongitud = Float.parseFloat(longitud);

                float latitudDifference = Math.abs(newLatitud - oldLatitud);
                float longitudDifference = Math.abs(newLongitud - oldLongitud);

                // Considerar una diferencia significativa para notificar (puedes ajustar el valor)
                float significantChange = 20f;

                return latitudDifference > significantChange || longitudDifference > significantChange;
            } catch (NumberFormatException e){
                return false; // Manejar la excepción si la conversión falla
            }
        }
    }

    public String getsaveLatitud(String machineName){
        return preferences.getString(machineName + KEY_LATITUD, null);
    }

    public String getSavelongitud(String machineName){
        return preferences.getString(machineName + KEY_LONGITUD, null);
    }

    private String otherData(String data){
        String[] parts = data.split("\\."); // Escapa el punto correctamente
        if (parts.length > 1) { // Verifica si se han encontrado puntos en la cadena
            String newData = getInfo2(parts[0]); // Utiliza parts[1] en lugar de parts[0] para obtener la segunda parte después de dividir
            return newData;
        } else {
            return ""; // Devuelve una cadena vacía si no se encuentra ningún punto
        }
    }


    private String data(String data){
        String[] parts = data.split("\\."); // Escapa el punto correctamente
        if (parts.length > 1) { // Verifica si se han encontrado puntos en la cadena
            String newData = getInfo(parts[1]); // Utiliza parts[1] en lugar de parts[0] para obtener la segunda parte después de dividir
            return newData;
        } else {
            return ""; // Devuelve una cadena vacía si no se encuentra ningún punto
        }
    }

    private String getInfo2(String data){
        String findvalue = "";
        for (int i = 0; i < data.length(); i++) {
            findvalue += data.charAt(i);
        }
        return findvalue;
    }

    private String getInfo(String data){
        String findvalue = "";
        for (int i = 0; i < data.length(); i++) {
            if (i == 2 || i == 3) {
                findvalue += data.charAt(i);
            }
            if (i == 4){
                break;
            }
        }
        return findvalue;
    }
    public void clearLocation(String machineName) {
        editor.remove(machineName + KEY_LATITUD);
        editor.remove(machineName + KEY_LONGITUD);
        editor.apply();
    }

}

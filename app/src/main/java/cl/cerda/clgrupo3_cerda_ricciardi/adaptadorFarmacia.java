package cl.cerda.clgrupo3_cerda_ricciardi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class adaptadorFarmacia extends BaseAdapter {

    private Context context;
    private List<Farmacia> listaFarmacias;
    private List<Farmacia> listaOriginal; // Para el filtrado

    public adaptadorFarmacia(Context context, List<Farmacia> listaFarmacias) {
        this.context = context;
        this.listaFarmacias = listaFarmacias;
        this.listaOriginal = new ArrayList<>();
        this.listaOriginal.addAll(listaFarmacias); // Copia la lista original
    }

    @Override
    public int getCount() {
        return listaFarmacias.size();
    }

    @Override
    public Object getItem(int position) {
        return listaFarmacias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.vista_adaptador_farmacia, parent, false);
        }

        TextView nombreFarmacia = convertView.findViewById(R.id.nombreFarmacia);
        TextView comunaFarmacia = convertView.findViewById(R.id.precioProducto); // Este ID es para la comuna según tu XML
        TextView direccionFarmacia = convertView.findViewById(R.id.direccionFarmacia);
        TextView horarioFarmacia = convertView.findViewById(R.id.horarioFarmacia);
        Button btnLlamar = convertView.findViewById(R.id.numtelefono);
        Button btnUbicacion = convertView.findViewById(R.id.ubicacion);

        Farmacia farmaciaActual = listaFarmacias.get(position);

        nombreFarmacia.setText(farmaciaActual.getNombre());
        comunaFarmacia.setText(farmaciaActual.getComuna());
        direccionFarmacia.setText(farmaciaActual.getDireccion());
        horarioFarmacia.setText(farmaciaActual.getHorarioCierre());

        // Manejo del botón de llamar
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = farmaciaActual.getTelefono();
                if (telefono != null && !telefono.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + telefono));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Teléfono no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Manejo del botón de ubicación
        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = farmaciaActual.getLatitud();
                double lon = farmaciaActual.getLongitud();
                // String direccion = farmaciaActual.getDireccion(); // Ya no la necesitamos para la URI del mapa

                if (lat != 0.0 && lon != 0.0) {
                    // Modificación: Elimina el parámetro 'q=' para que solo use latitud y longitud.
                    String uri = "geo:" + lat + "," + lon; // CAMBIO CLAVE AQUÍ
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps"); // Abrir con Google Maps
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    } else {
                        // Modificación: Elimina el parámetro 'q=' para que solo use latitud y longitud en el fallback web.
                        // Usamos el formato estandar para Google Maps web con coordenadas: maps?q=loc:LAT,LNG
                        String url = "http://maps.google.com/maps?q=loc:" + lat + "," + lon; // CAMBIO CLAVE AQUÍ
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                } else {
                    Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    // Método de filtrado
    public void filter(String charText) {
        charText = charText.toLowerCase();
        listaFarmacias.clear();
        if (charText.length() == 0) {
            listaFarmacias.addAll(listaOriginal);
        } else {
            for (Farmacia fm : listaOriginal) {
                if (fm.getNombre().toLowerCase().contains(charText)) {
                    listaFarmacias.add(fm);
                }
            }
        }
        notifyDataSetChanged();
    }
}
package com.example.heartcare;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;
import static com.example.heartcare.choice_screen.dayDeb;
import static com.example.heartcare.choice_screen.dayFin;
import static com.example.heartcare.choice_screen.hourDeb;
import static com.example.heartcare.choice_screen.hourFin;
import static com.example.heartcare.choice_screen.minDeb;
import static com.example.heartcare.choice_screen.minFin;
import static com.example.heartcare.choice_screen.monthDeb;
import static com.example.heartcare.choice_screen.monthFin;
import static com.example.heartcare.choice_screen.yearDeb;
import static com.example.heartcare.choice_screen.yearFin;

public class Chart1 extends AppCompatActivity {

    LineChart mpLineChart; //graphique



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart1);


        List<String> listLines = new ArrayList<String>(); //Création de la liste pour mettre toutes les lignes luent par le buffer (1 x et 1 y par ligne séparés par des ',')
       listLines=choice_screen.accessLocal.recup(yearDeb,monthDeb,dayDeb,hourDeb,minDeb,yearFin,monthFin,dayFin,hourFin,minFin);



        /**Conversion des lignes en liste de Entry (donnees de la courbe)*/
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        if (listLines.size() == 0) {
            Toast.makeText(this, "Aucune donnée disponible pour cet intervalle", Toast.LENGTH_LONG).show();
        } else{
            int taille = listLines.size();
        String deb = listLines.get(0);
        String[] dateDebut = deb.split(",")[0].split("/");
        String[] heureDebut = dateDebut[3].split(":");
        float debut = Float.parseFloat(heureDebut[1]) + 60 * Float.parseFloat(heureDebut[0]) + 1440 * Float.parseFloat(dateDebut[2]) + 1440 * nbJoursMois(dateDebut[1]) * Float.parseFloat(dateDebut[1]) + 525600 * Float.parseFloat(dateDebut[0]);

        String fi = listLines.get(taille - 1);
        String[] dateFin = (fi.split(",")[0]).split("/");
        String[] heureFin = dateFin[3].split(":");
        float fin = Float.parseFloat(heureFin[1]) + 60 * Float.parseFloat(heureFin[0]) + 1440 * Float.parseFloat(dateFin[2]) + 1440 * nbJoursMois(dateFin[1]) * Float.parseFloat(dateFin[1]) + 525600 * Float.parseFloat(dateFin[0]);

        String[] xValues;   /**La valeur affichee sur l'axe à l'abscisses i sera
         la case à l'indice i du tableau*/


        if (fin - debut < 1500) //pour un intervalle d'un jour
        {
            //Initialisation de xValues
            xValues = new String[(int) distance("heure", deb, fi) + 1];
            for (int i = 0; i < xValues.length; i++)
                xValues[i] = "";

            //Remplissage dans entry et xValues
            if (!listLines.isEmpty()) {
                String[] s = listLines.get(0).split(",");
                ;
                float y;
                float x = 0;
                String[] date = s[0].split("/");
                ;
                String[] heure = date[3].split(":");

                for (String line : listLines) {
                    s = line.split(",");
                    date = s[0].split("/");
                    heure = date[3].split(":");
                    x = distance("heure", deb, line);
                    y = Float.parseFloat(s[1]);
                    dataVals.add(new Entry(x, y));
                    xValues[(int) x] = date[3];
                }
            }
        } else if (fin - debut < 50400) {
            /**On remplit lines2 avec la moyenne de fréquence cardiaque pour chaque jour*/
            List<String> lines2 = new ArrayList<String>();
            String[] dateJ = listLines.get(0).split(",")[0].split("/");    //On va regarder a chaque fois si dateJ est egal a previous
            String previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
            String[] previousTab = dateJ;
            String[] jour;
            float m = 0;
            float j = 0;
            for (String line : listLines) {
                jour = line.split(",");
                dateJ = jour[0].split("/");
                if (!(previousTab[0].equals(dateJ[0]) && previousTab[1].equals(dateJ[1]) && previousTab[2].equals(dateJ[2]))) {
                    m /= j;
                    j = 0;
                    lines2.add(previous + "," + String.valueOf(m));
                    previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
                    previousTab = dateJ;
                    m = 0;
                }
                m += Float.parseFloat(jour[1]);
                j++;
            }
            m /= j;
            j = 0;
            lines2.add(previous + "," + String.valueOf(m));
            previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];

            /**Initialisation du tableau pour l'echelle*/
            xValues = new String[(int) distance("jour", deb, fi) + 1];
            for (int i = 0; i < xValues.length; i++)
                xValues[i] = "";

            /**Ajout des nouvelles valeurs dans entry et xValues*/
            if (!lines2.isEmpty()) {
                String[] s;
                float y;
                float x = 0;
                String[] date;

                s = lines2.get(0).split(",");
                date = s[0].split("/");

                for (String line : lines2) {
                    s = line.split(",");
                    date = s[0].split("/");
                    x = distance("jour", deb, line);
                    y = Float.parseFloat(s[1]);
                    dataVals.add(new Entry(x, y));
                    xValues[(int) x] = date[2] + "/" + date[1];
                }
            }
        } else if (fin - debut < 535680) {
            /**On remplit lines2 avec la moyenne de fréquence cardiaque pour chaque mois*/
            List<String> lines2 = new ArrayList<String>();
            String[] dateJ = listLines.get(0).split(",")[0].split("/");    //On va regarder a chaque fois si dateJ est egal a previous
            String previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
            String[] previousTab = dateJ;
            String[] jour;
            float m = 0;
            float j = 0;
            for (String line : listLines) {
                jour = line.split(",");
                dateJ = jour[0].split("/");
                if (!(previousTab[0].equals(dateJ[0]) && previousTab[1].equals(dateJ[1]))) {
                    m /= j;
                    j = 0;
                    lines2.add(previous + "," + String.valueOf(m));
                    previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
                    previousTab = dateJ;
                    m = 0;
                }
                m += Float.parseFloat(jour[1]);
                j++;
            }
            m /= j;
            j = 0;
            lines2.add(previous + "," + String.valueOf(m));
            previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];

            /**Initialisation du tableau pour l'echelle*/
            xValues = new String[(int) (distance("mois", deb, fi) + 1)];
            for (int i = 0; i < xValues.length; i++)
                xValues[i] = "";


            /**Ajout des nouvelles valeurs dans entry et xValues*/
            if (!lines2.isEmpty()) {
                String[] s;
                float y;
                float x = 0;
                String[] date;

                s = lines2.get(0).split(",");
                date = s[0].split("/");


                for (String line : lines2) {
                    s = line.split(",");
                    date = s[0].split("/");
                    x = distance("mois", deb, line);
                    y = Float.parseFloat(s[1]);
                    dataVals.add(new Entry(x, y));
                    xValues[(int) x] = date[1] + "/" + date[0];
                }
            }
        } else {
            /**On remplit lines2 avec la moyenne de fréquence cardiaque pour chaque mois*/
            List<String> lines2 = new ArrayList<String>();
            String[] dateJ = listLines.get(0).split(",")[0].split("/");    //On va regarder a chaque fois si dateJ est egal a previous
            String previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
            String[] previousTab = dateJ;
            String[] jour;
            float m = 0;
            float j = 0;
            for (String line : listLines) {
                jour = line.split(",");
                dateJ = jour[0].split("/");
                if (!(previousTab[0].equals(dateJ[0]))) {
                    m /= j;
                    j = 0;
                    lines2.add(previous + "," + String.valueOf(m));
                    previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];
                    previousTab = dateJ;
                    m = 0;
                }
                m += Float.parseFloat(jour[1]);
                j++;
            }
            m /= j;
            j = 0;
            lines2.add(previous + "," + String.valueOf(m));
            previous = dateJ[0] + "/" + dateJ[1] + "/" + dateJ[2] + "/" + dateJ[3];

            /**Initialisation du tableau pour l'echelle*/
            xValues = new String[(int) (distance("annee", deb, fi) + 1)];
            for (int i = 0; i < xValues.length; i++)
                xValues[i] = "";


            /**Ajout des nouvelles valeurs dans entry et xValues*/
            if (!lines2.isEmpty()) {
                String[] s;
                float y;
                float x = 0;
                String[] date;

                s = lines2.get(0).split(",");
                date = s[0].split("/");


                for (String line : lines2) {
                    s = line.split(",");
                    date = s[0].split("/");
                    x = distance("annee", deb, line);
                    y = Float.parseFloat(s[1]);
                    dataVals.add(new Entry(x, y));
                    xValues[(int) x] = date[0];
                }
            }
        }


        /**Affichage de la courbe*/
        mpLineChart = (LineChart) findViewById(R.id.chart1);                       //association graphique view
        LineDataSet lineDataSet1;
        lineDataSet1 = new LineDataSet(dataVals, "Data set 1");     //creation d'une courbe avec donnees et titre

        //ArrayList de courbes
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if (lineDataSet1 != null) {
            dataSets.add(lineDataSet1);

            //ajout des courbes dans mpLineChart
            LineData data = new LineData(dataSets);
            XAxis xaxis = mpLineChart.getXAxis();
            xaxis.setValueFormatter((new IndexAxisValueFormatter(xValues)));
            mpLineChart.setData(data);
            mpLineChart.invalidate();
        }
    }
    }


    public static float nbJoursMois(String mois)
    {
        if(mois.equals("02"))
        {
            return 28;
        }
        if(mois.equals("01")||mois.equals("03")||mois.equals("05")||mois.equals("07")||mois.equals("08")||mois.equals("10")||mois.equals("12"))
        {
            return 31;
        }
        return 30;
    }

    public static float distance(String mode, String debut , String fin)
    {
        String[] dateDebut = debut.split(",")[0].split("/");
        String[] heureDeb = dateDebut[3].split(":");
        String[] dateFin = fin.split(",")[0].split("/");
        String[] heureF = dateFin[3].split(":");
        float anneeDebut = Float.parseFloat(dateDebut[0]);
        float moisDebut = Float.parseFloat(dateDebut[1]);
        float jourdebut = Float.parseFloat(dateDebut[2]);
        float heureDebut = Float.parseFloat(heureDeb[0]);
        float minuteDebut = Float.parseFloat(heureDeb[1]);
        float anneeFin = Float.parseFloat(dateFin[0]);
        float moisFin = Float.parseFloat(dateFin[1]);
        float jourFin = Float.parseFloat(dateFin[2]);
        float heureFin = Float.parseFloat(heureF[0]);
        float minuteFin = Float.parseFloat(heureF[1]);
        float nbJoursMois=nbJours(debut,fin);


        if(mode.equals("heure"))
        {
            return minuteFin-minuteDebut+60*(heureFin-heureDebut)+1440*nbJoursMois+525600*(anneeFin-anneeDebut);
        }
        if(mode.equals("jour"))
        {
            return nbJoursMois;
        }
        if(mode.equals("mois"))
        {
            return moisFin-moisDebut + 12*(anneeFin-anneeDebut);
        }
        return anneeFin-anneeDebut;
    }


    public static float nbJours(String debut, String fin)
    {
        String[] dateDebut = debut.split(",")[0].split("/");
        String[] dateFin = fin.split(",")[0].split("/");
        float mois=0;

        float moisDebut = Float.parseFloat(dateDebut[1]);
        float moisFin = Float.parseFloat(dateFin[1]);

        if(moisFin-moisDebut==0)
        {
            return Float.parseFloat(dateFin[2])-Float.parseFloat(dateDebut[2]);
        }
        if(moisFin-moisDebut>0)
        {
            mois = nbJoursMois(dateDebut[1])-Float.parseFloat(dateDebut[2]);
            for(int i=(int)moisDebut ; i<(int) moisFin-1 ; i++)
            {
                if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12)
                {
                    mois+=31;
                }
                else if(i==4 || i==6 || i==9 || i==11)
                {
                    mois+=30;
                }
                else
                {
                    mois+= 28;
                }
            }
            return mois+Float.parseFloat(dateFin[2]);
        }
        mois = nbJoursMois(dateDebut[1])-Float.parseFloat(dateDebut[2]);
        for(int i=(int)moisDebut ; (i+1)%12!=((int) moisFin) ; i++)
        {

            if(i==1 || i==3 || i==5 || i==7 || i==8 || i==10 || i==12)
            {
                mois+=31;
            }
            else if(i==4 || i==6 || i==9 || i==11)
            {
                mois+=30;
            }
            else
            {
                mois+= 28;
            }
        }
        return (mois+Float.parseFloat(dateFin[2]));
    }
}

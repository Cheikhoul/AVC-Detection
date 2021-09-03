
let clients
function ajouterPatients(patients){

  patients = patients.patients;
  let tblBody = document.getElementById("table");
  clients = patients
  for(let i=0 ; i<patients.length ; i++)
  {
    let tr=document.createElement('tr');
    tr.setAttribute('id', patients[i].id)
    let td=document.createElement('td');
    tr.appendChild(td);
    let div=document.createElement('div');
    div.setAttribute('class','radio');
    td.appendChild(div);
    let label = document.createElement('label');
    div.appendChild(label);
    let input = document.createElement('input');
    input.setAttribute('type','radio');
    input.setAttribute('name' , 'choixPatient');
    input.setAttribute('id', 'patient' + i)
    label.appendChild(input);
    let td2 = document.createElement('td');
    td2.innerHTML=patients[i].name + " " + patients[i].surname;
    tr.appendChild(td2);
    tblBody.appendChild(tr);
    tr.setAttribute('draggable','true');
    tr.addEventListener('dragstart', function(e) {
      e.dataTransfer.setData('text/plain', patients[i].id+"/"+patients[i].name+"/"+patients[i].surname);
      document.getElementById('corbeille').style.visibility='visible';
    });
    tr.addEventListener('dragend', function(e) {
      document.getElementById('corbeille').style.visibility='hidden';
    });

  }

}
function defaultUncheck(){
  var checkboxes = document.getElementsByTagName('input');

  for (var i=0; i<checkboxes.length; i++)  {
    if (checkboxes[i].type == 'checkbox')   {
      checkboxes[i].checked = false;
    }
  }
}

function completerDate(id)
{
  const date=document.getElementById(id);
  if(event.keyCode !== 8)
  {
    tab = date.value.split('/');
    if(tab.length===1)
    {
      if(tab[0].length===4)
      {
        date.value+='/';
      }
    }
    else if(tab.length===2)
    {
      if(tab[1].length===2)
      {
        date.value+='/';
      }
    }
  }
}

function completerHeure(id)
{
  const heure=document.getElementById(id);
  if(event.keyCode !== 8)
  {
    tab=heure.value.split(':');
    if(tab.length===1)
    {
      if(tab[0].length===2)
      {
        heure.value+=':';
      }
    }
  }
}

function getElementCoche(){
  for(let i=0 ; i<clients.length ; i++)
  {
    if(document.getElementById('patient'+i).checked)
    {
      return i;
    }
  }
}


function dateConforme(date)
{
  const paragraphe = document.getElementById(date);
  const tab=paragraphe.value.split('/');
  if(tab.length!==3)
  {
    return false;
  }
  else if(tab[0].length!==4 || tab[1].length!==2 || tab[2].length !== 2)
  {
    return false;
  }
  else if(isNaN(tab[0]) || isNaN(tab[1]) || isNaN(tab[2]))
  {
    return false;
  }
  else if(tab[1]>12 || tab[1]<1)
  {
    document.getElementById(date +'MauvaisFormat').textContent=tab[1] + ' ne correspond pas à un numéro de mois';
    return false;
  }
  else if(tab[2]<1 || tab[2]>31)
  {
    document.getElementById(date +'MauvaisFormat').textContent=tab[2] + 'ne correspond pas à un numéro de jour';
    return false;
  }
  return true;
}

function heureConforme(heure)
{
  const paragraphe = document.getElementById(heure);
  const tab=paragraphe.value.split(':');
  if(tab.length!==2)
  {
    return false;
  }
  else if(tab[0].length!==2 || tab[1].length!==2)
  {
    return false;
  }
  else if(isNaN(tab[0]) || isNaN(tab[1]))
  {
    return false;
  }
  else if(tab[0]>23 || tab[0]<0)
  {
    document.getElementById(heure +'MauvaisFormat').textContent=tab[0] + ' ne correspond pas à une heure';
    return false;
  }
  else if(tab[1]>59 || tab[1]<0)
  {
    document.getElementById(heure +'MauvaisFormat').textContent=tab[1] + ' ne correspond pas à une minute ';
    return false;
  }
  return true;
}

function auMoinsUnElementCoche()
{
  for(let i=0 ; i<clients.length ; i++)
  {
    if(document.getElementById('patient'+i).checked)
    {
      return true;
    }
  }
  return false;
}

function verificationDonneesConformes(){
  let donneesConformes = true;

  if(!dateConforme('dateDebut'))
  {
    document.getElementById('dateDebutMauvaisFormat').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('dateDebutMauvaisFormat').style.display='none';
  }
  if(!dateConforme('dateFin'))
  {
    document.getElementById('dateFinMauvaisFormat').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('dateFinMauvaisFormat').style.display='none';
  }
  if(!heureConforme('heureDebut'))
  {
    document.getElementById('heureDebutMauvaisFormat').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('heureDebutMauvaisFormat').style.display='none';
  }
  if(!heureConforme('heureFin'))
  {
    document.getElementById('heureFinMauvaisFormat').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('heureFinMauvaisFormat').style.display='none';
  }
  if(!auMoinsUnElementCoche())
  {
    document.getElementById('selecPatient').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('selecPatient').style.display='none';
  }
  if(!document.getElementById('frequenceCardiaque').checked && !document.getElementById('tensionArterielle').checked)
  {
    document.getElementById('pasDeCourbe').style.display='block';
    donneesConformes=false;
  }
  else {
    document.getElementById('pasDeCourbe').style.display='none';
    document.getElementById('pasDeCourbe').style.display='none';
  }
  return donneesConformes;
}

/**
 * Sending delete request containing the id
 * of the patient to be deleted to the server
 * @param patient
 */
function supprimerPatient(patient) {
  $.ajax( {
    data :{
      id: patient[0] // 0 index for id
    },
    type : 'POST',
    url : '/delete_pat'
  }).done(function (data) {
      if(data.msg){
        if(data.msg ==='Success'){
          alert(patient[1] + ' ' + patient[2] + ' a été supprimé de la base de données');
          element = document.getElementById(patient[0])
          element.parentNode.removeChild(element)
        }
        else{
        alert('erreur lors de la suppression de ' + patient[1]);
      }
      }
      else{
        alert("Un problem s'est produit ");
      }
    })
}

function deleteChosen() {
  let patient;
  const corbeille = document.getElementById('corbeille');
  corbeille.addEventListener('dragover', function (e) {
    e.preventDefault();
    corbeille.style.color = 'red';
  });

  corbeille.addEventListener('dragleave', function () {
    corbeille.style.color = 'black';
  });
  corbeille.addEventListener('drop', function (e) {
    e.preventDefault();
    corbeille.style.color = 'black';
    patient = e.dataTransfer.getData('text/plain').split('/');
    console.log(patient);
    if (confirm("Attention : " + patient[1] + patient[2] + " sera définitivement supprimé de compte")) {
      supprimerPatient(patient);
    }
  });
}


function getDonnees()
{
  if(verificationDonneesConformes())
  {
    const dateDebut=document.getElementById('dateDebut').value;
    const heureDebut=document.getElementById('heureDebut').value;
    const dateFin=document.getElementById('dateFin').value;
    const heureFin=document.getElementById('heureFin').value;
    const freq = document.getElementById('frequenceCardiaque').checked;
    const tens=document.getElementById('tensionArterielle').checked;

    const client = getElementCoche();

    $.ajax({
      data : {
        dateDeb: dateDebut,
        dateFin: dateFin,
        heureDebut: heureDebut,
        heureFin: heureFin,
        id: clients[client].id,
        freq: freq,
        tens: tens
      },
      type: 'POST',
      url: '/doctors_view'
    }).done(function (data) {
      if(data.msg){
        if(data.msg ==='Success'){
          if(freq) {
            afficher_graph("ChartFrequency", data.heart_f, data.dates_hf, 1)
          }
          if(tens) {
            afficher_graph("ChartPressure", data.blood_p, data.dates_bd, 2)
          }
        }
      }
      else{
        console.log("bad request")
      }
    })

  }
}


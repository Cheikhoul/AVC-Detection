
function afficher_graph(id, data, labels, type){
	console.log(labels);
	document.getElementById("graphic").style.display = "block"
	let ctx = document.getElementById(id).getContext('2d');
	let label, color;
	if(type === 1) {
		label = 'fréquence cardiaque'
		color = 'rgba(255, 0, 0, 1)'
	}
	else {
		label = 'tension arterielle'
		color = 'rgba(0, 0, 255, 1)'
	}
	let scatterChart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: labels,
			datasets: [{
				label: label,
				borderColor: color,
				lineTension: 0,
				backgroundColor: 'rgba(0, 0, 0, 0)',
				borderWidth: 1,
				pointRadius: 0,
				data: data
			}]
		},
		options: {
			scales: {
				xAxes: [{
					position: 'bottom'
				}]
			}
		}
	});
}


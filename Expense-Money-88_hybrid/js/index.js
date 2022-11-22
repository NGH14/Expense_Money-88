let ERROR = 'ERROR';
let currentTripId = 'currentTripId';
let db = window.openDatabase('FGW', '1.0', 'FGW', 20000);

$(document).on('ready', onDeviceReady);

$(document).on('click', '#home #panel-open', function () {
	$('#home #panel').panel('open');
});

$(document).on('click', '#create #panel-open', function () {
	$('#create #panel').panel('open');
});

$(document).on('click', '#list #panel-open', function () {
	$('#list #panel').panel('open');
});

$(document).on('click', '#about #panel-open', function () {
	$('#about #panel').panel('open');
});

$(document).on('click', '#setting #btn-backup', function () {
	let message = 'Backup to cloud successfully.';

	log(message);
	toast(message);
});

$(document).on('click', '#setting #btn-reset', function () {
	db.transaction(function (tx) {
		let query = `DROP TABLE Expense`;
		tx.executeSql(
			query,
			[],
			function (tx, result) {
				log(`Drop table 'Expense' successfully.`);
			},
			transactionError,
		);

		query = `DROP TABLE Trip`;
		tx.executeSql(
			query,
			[],
			function (tx, result) {
				log(`Drop table 'Trip' successfully.`);

				prepareDatabase(db);

				toast('Reset successfully.');
			},
			transactionError,
		);
	});
});

// Page CREATE
$(document).on('submit', '#create #frm-register', confirmTrip);
$(document).on('submit', '#create #frm-confirm', registerTrip);
$(document).on('click', '#create #frm-confirm #edit', function () {
	$('#create #frm-confirm').popup('close');
});

// Page LIST
$(document).on('pagebeforeshow', '#list', showList);

$(document).on('submit', '#list #frm-search', search);

$(document).on('keyup', $('#list #txt-filter'), filterTrip);

$(document).on('click', '#list #btn-reset', showList);
$(document).on('click', '#list #btn-filter-popup', openFormSearch);
$(document).on('click', '#list #list-trip li a', navigatePageDetail);

// Page DETAIL
$(document).on('pagebeforeshow', '#detail', showDetail);

$(document).on('click', '#detail #btn-toggle-expense', function () {
	let expenseDisplay = $('#detail #expense').css('display');

	$('#detail #expense').css(
		'display',
		expenseDisplay == 'none' ? 'block' : 'none',
	);
});

$(document).on('click', '#detail #btn-update-popup', showUpdate);
$(document).on('click', '#detail #btn-delete-popup', function () {
	changePopup($('#detail #option'), $('#detail #frm-delete'));
});

$(document).on('click', '#detail #frm-update #cancel', function () {
	$('#detail #frm-update').popup('close');
});

$(document).on('click', '#detail #frm-add-expense #cancel', function () {
	$('#detail #frm-add-expense').popup('close');
});

$(document).on('submit', '#detail #frm-update', updateTrip);
$(document).on('submit', '#detail #frm-delete', deleteTrip);
$(document).on('submit', '#detail #frm-add-expense', addExpense);
$(document).on('keyup', '#detail #frm-delete #txt-confirm', confirmDeleteTrip);

function onDeviceReady() {
	log(`Device is ready.`);

	prepareDatabase(db);
}

function log(message, type = 'INFO') {
	console.log(`${new Date()} [${type}] ${message}`);
}

function changePopup(sourcePopup, destinationPopup) {
	let afterClose = function () {
		destinationPopup.popup('open');
		sourcePopup.off('popupafterclose', afterClose);
	};

	sourcePopup.on('popupafterclose', afterClose);
	sourcePopup.popup('close');
}

function confirmTrip(e) {
	e.preventDefault();

	log('Open the confirmation popup.');

	$('#create #frm-confirm #name').text(
		$('#create #frm-register #name').val(),
	);
	$('#create #frm-confirm #destination').text(
		$('#create #frm-register #destination').val(),
	);
	$('#create #frm-confirm #date').text(
		$('#create #frm-register #date').val(),
	);
	$('#create #frm-confirm #description').text(
		$('#create #frm-register #description').val(),
	);
	$('#create #frm-confirm #risk').text(
		$('#create #frm-register #risk').val(),
	);

	$('#create #frm-confirm').popup('open');
}

function registerTrip(e) {
	e.preventDefault();

	let name = $('#create #frm-register #name').val();
	let destination = $('#create #frm-register #destination').val();
	let date = $('#create #frm-register #date').val();
	let description = $('#create #frm-register #description').val();
	let risk = $('#create #frm-register #risk').val();
	var dateObj = new Date();
	var month = dateObj.getUTCMonth() + 1;
	var day = dateObj.getUTCDate();
	var year = dateObj.getUTCFullYear();

	newdate = year + '/' + month + '/' + day;

	db.transaction(function (tx) {
		let query = `INSERT INTO Trip (Name, Destination, Description, Risk, Created,Date) VALUES (?, ?, ?, ?, ?,julianday('${date}'))`;
		tx.executeSql(
			query,
			[name, destination, description, risk, newdate],
			transactionSuccess,
			transactionError,
		);

		function transactionSuccess(tx, result) {
			let message = `Added trip '${name}'.`;

			log(message);
			toast(message);

			$('#create #frm-register').trigger('reset');
			$('#create #frm-register #name').focus();

			$('#create #frm-confirm').popup('close');
		}
	});
}

function showList() {
	db.transaction(function (tx) {
		let query = `SELECT *, date(Date) AS DateConverted FROM Trip`;

		tx.executeSql(query, [], transactionSuccess, transactionError);

		function transactionSuccess(tx, result) {
			log(`Get list of trips successfully.`);
			displayList(result.rows);
		}
	});
}

function navigatePageDetail(e) {
	e.preventDefault();

	let id = $(this).data('details').Id;
	localStorage.setItem(currentTripId, id);

	$.mobile.navigate('#detail', { transition: 'none' });
}

function showDetail() {
	let id = localStorage.getItem(currentTripId);

	db.transaction(function (tx) {
		let query = `SELECT *, date(Date) AS DateConverted FROM Trip WHERE Id = ?`;

		tx.executeSql(query, [id], transactionSuccess, transactionError);

		function transactionSuccess(tx, result) {
			if (result.rows[0] != null) {
				log(
					`Get details of trip '${result.rows[0].name}' successfully.`,
				);

				$('#detail #detail #name').text(result.rows[0].Name);
				$('#detail #detail #destination').text(
					result.rows[0].Destination,
				);
				$('#detail #detail #description').text(
					result.rows[0].Description,
				);
				$('#detail #detail #risk').text(result.rows[0].Risk);
				$('#detail #detail #created').text(result.rows[0].Created);

				$('#detail #detail #date').text(result.rows[0].DateConverted);

				showExpense();
			} else {
				let errorMessage = 'Trip not found.';

				log(errorMessage, ERROR);

				$('#detail #detail #name').text(errorMessage);
				$('#detail #btn-update').addClass('ui-disabled');
				$('#detail #btn-delete-confirm').addClass('ui-disabled');
			}
		}
	});
}

function confirmDeleteTrip() {
	let text = $('#detail #frm-delete #txt-confirm').val();

	if (text == 'confirm delete') {
		$('#detail #frm-delete #btn-delete').removeClass('ui-disabled');
	} else {
		$('#detail #frm-delete #btn-delete').addClass('ui-disabled');
	}
}

function deleteTrip(e) {
	e.preventDefault();

	let tripId = localStorage.getItem(currentTripId);

	db.transaction(function (tx) {
		let name = '';

		let query = 'SELECT * FROM Trip WHERE Id = ?';
		tx.executeSql(
			query,
			[tripId],
			function (tx, result) {
				name = result.rows[0].Name;
			},
			transactionError,
		);

		query = 'DELETE FROM Expense WHERE TripId = ?';
		tx.executeSql(
			query,
			[tripId],
			function (tx, result) {
				log(`Delete expenses of trip '${tripId}' successfully.`);
			},
			transactionError,
		);

		query = 'DELETE FROM Trip WHERE Id = ?';
		tx.executeSql(
			query,
			[tripId],
			function (tx, result) {
				let message = `Deleted trip '${name}'.`;

				log(message);
				toast(message);

				$('#detail #frm-delete').trigger('reset');

				$.mobile.navigate('#list', { transition: 'none' });
			},
			transactionError,
		);
	});
}

function addExpense(e) {
	e.preventDefault();

	let tripId = localStorage.getItem(currentTripId);
	let type = $('#detail #frm-add-expense #type').val();
	let amount = parseInt($('#detail #frm-add-expense #amount').val());
	let date = $('#detail #frm-add-expense #date').val();
	let time = $('#detail #frm-add-expense #time').val();
	let comment = $('#detail #frm-add-expense #comment').val();

	db.transaction(function (tx) {
		let query = `INSERT INTO Expense (Type, Amount, Comment, TripId, Date, Time) VALUES (?, ?, ?, ?, julianday('${date}'), julianday('${time}'))`;
		tx.executeSql(
			query,
			[type, amount, comment, tripId],
			transactionSuccess,
			transactionError,
		);

		function transactionSuccess(tx, result) {
			let message = `Added expense '${amount.toLocaleString(
				'en-US',
			)} VNĐ (${type})'.`;

			log(message);
			toast(message);

			$('#detail #frm-add-expense').trigger('reset');
			$('#detail #frm-add-expense').popup('close');

			showExpense();
		}
	});
}

function showExpense() {
	let id = localStorage.getItem(currentTripId);

	db.transaction(function (tx) {
		let query = `SELECT *, date(Date) AS DateConverted, time(Time) AS TimeConverted FROM Expense WHERE TripId = ? ORDER BY Date DESC, Time DESC`;

		tx.executeSql(query, [id], transactionSuccess, transactionError);

		function transactionSuccess(tx, result) {
			log(`Get list of expenses successfully.`);

			let expenseList = '';
			let currentDate = '';
			for (let expense of result.rows) {
				if (currentDate != expense.DateConverted) {
					expenseList += `<div class='list-date'>${expense.DateConverted}</div>`;
					currentDate = expense.DateConverted;
				}

				expenseList += `
                    <div class='list'>
                        <table style='white-space: nowrap; width: 100%;'>
                            <tr style='height: 25px;'>
                                <td>${
									expense.Type
								}: ${expense.Amount.toLocaleString(
					'en-US',
				)} VNĐ</td>
                                <td style='text-align: right;'>${
									expense.TimeConverted
								}</td>
                            </tr>

                            <tr>
                                <td colspan='2'><em>${expense.Comment}</em></td>
                            </tr>
                        </table>
                    </div>`;
			}

			expenseList += `<div class='list end-list'>You've reached the end of the list.</div>`;

			$('#detail #expense #list').empty().append(expenseList);

			log(`Show list of expenses successfully.`);
		}
	});
}

function showUpdate() {
	let id = localStorage.getItem(currentTripId);

	db.transaction(function (tx) {
		let query = `SELECT *, date(Date) as DateConverted FROM Trip WHERE Id = ?`;

		tx.executeSql(query, [id], transactionSuccess, transactionError);

		function transactionSuccess(tx, result) {
			if (result.rows[0] != null) {
				log(
					`Get details of trip '${result.rows[0].Name}' successfully.`,
				);

				$(`#detail #frm-update #name`).val(result.rows[0].Name);
				$(`#detail #frm-update #destination`).val(
					result.rows[0].Destination,
				);
				$(`#detail #frm-update #risk`)
					.val(result.rows[0].Risk)
					.slider('refresh');
				$(`#detail #frm-update #date`).val(
					result.rows[0].DateConverted,
				);
				$(`#detail #frm-update #description`).val(
					result.rows[0].Description,
				);

				changePopup($('#detail #option'), $('#detail #frm-update'));
			}
		}
	});
}

function updateTrip(e) {
	e.preventDefault();

	let id = localStorage.getItem(currentTripId);
	let name = $('#detail #frm-update #name').val();
	let destination = $('#detail #frm-update #destination').val();
	let date = $('#detail #frm-update #date').val();
	let description = $('#detail #frm-update #description').val();
	let risk = $('#detail #frm-update #risk').val();

	db.transaction(function (tx) {
		let query = `UPDATE Trip SET Name = ?, Destination = ?, Description = ?, Risk = ?, Date = julianday('${date}') WHERE Id = ?`;

		tx.executeSql(
			query,
			[name, destination, description, risk, id],
			transactionSuccess,
			transactionError,
		);

		function transactionSuccess(tx, result) {
			let message = `Updated trip '${name}'.`;

			log(message);
			toast(message);

			showDetail();

			$('#detail #frm-update').popup('close');
		}
	});
}

function filterTrip() {
	let filter = $('#list #txt-filter').val().toLowerCase();
	let li = $('#list #list-trip ul li');

	for (let i = 0; i < li.length; i++) {
		let a = li[i].getElementsByTagName('a')[0];
		let text = a.textContent || a.innerText;

		li[i].style.display =
			text.toLowerCase().indexOf(filter) > -1 ? '' : 'none';
	}
}

function openFormSearch(e) {
	e.preventDefault();
	$('#list #frm-search').popup('open');
}

function search(e) {
	e.preventDefault();

	let name = $('#list #frm-search #name').val();
	let destination = $('#list #frm-search #destination').val();
	let date = $('#list #frm-search #date').val();

	db.transaction(function (tx) {
		let query = `SELECT *, date(Date) as DateConverted FROM Trip WHERE`;

		query += name ? ` Name LIKE "%${name}%"   AND` : '';
		query += destination
			? ` Destination LIKE "%${destination}%"   AND`
			: '';
		query += date ? ` Date = julianday('${date}')   AND` : '';

		query = query.substring(0, query.length - 6);

		tx.executeSql(query, [], transactionSuccess, transactionError);

		function transactionSuccess(tx, result) {
			log(`Search trips successfully.`);

			displayList(result.rows);

			$('#list #frm-search').trigger('reset');
			$('#list #frm-search').popup('close');
		}
	});
}

function displayList(list) {
	let tripList = `<ul id='list-trip' data-role='listview' class='ui-nodisc-icon ui-alt-icon'>`;

	tripList +=
		list.length == 0 ? '<li><h2>There is no business trip.</h2></li>' : '';

	for (let trip of list) {
		tripList += `<li><a data-details='{"Id" : ${trip.Id}}'>
                <h5>${trip.Name}</h5>
                <p>${trip.DateConverted}  (${trip.Destination})</p>
            </a></li>`;
	}
	tripList += `</ul>`;

	$('#list-trip')
		.empty()
		.append(tripList)
		.listview('refresh')
		.trigger('create');

	log(`Show list of trips successfully.`);
}

function toast(message) {
	$(
		"<div class='ui-loader ui-overlay-shadow ui-body-e ui-corner-all'><p>" +
			message +
			'</p></div>',
	)
		.css({
			background: '#F5F5F5',
			color: 'black',
			'font-size': '14px',
			'font-weight': '700',
			display: 'block',
			opacity: 1,
			position: 'fixed',
			padding: '2px',
			'border-radius': '25px',
			'text-align': 'center',
			'box-shadow': 'box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;',
			'-moz-box-shadow': 'none',
			'-webkit-box-shadow': 'none',
			width: '250px',
			left: ($(window).width() - 254) / 2,
			top: $(window).height() - 810,
		})

		.appendTo($.mobile.pageContainer)
		.delay(3500)

		.fadeOut(400, function () {
			$(this).remove();
		});
}

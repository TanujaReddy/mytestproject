var seaLoginParams = {};
var seaLoginParams1 = {};

var setParams = function(country, errorMessage) {
	seaLoginParams['country'] = country;
	seaLoginParams['errorMessage'] = errorMessage;

};

var setParamsLogin = function(country, errorMessage) {
	seaLoginParams1['country'] = country;
	seaLoginParams1['errorMessage'] = errorMessage;

};

$(document).ready(
		function(e) {

			$("#sea-login-btn").click(
					function(e) {

						var username = $("input[name='sea-ato-username']")
								.val();
						var password = $("input[name='sea-ato-password']")
								.val();
						$("input[name='sea-ato-username']").prop('disabled',
								true);
						$("input[name='sea-ato-password']").prop('disabled',
								true);
						$("#sea-login-btn").prop('disabled', true);
						login(username, password, seaLoginParams1.country,
								seaLoginParams1.errorMessage,
								'sea-ato-username', 'sea-ato-password',
								'sea-login-btn');

					});

			$("#sea-logout-btn").click(function(e) {
				$("#sea-logout-btn").prop('disabled', true);
				logoutSEA();

			});
			
			$("#sea-moile-login-btn").click(
					function(e) {

						var username = $("input[name='sea-mobile-ato-username']")
								.val();
						var password = $("input[name='sea-mobile-ato-password']")
								.val();
						$("input[name='sea-mobile-ato-username']").prop('disabled',
								true);
						$("input[name='sea-mobile-ato-password']").prop('disabled',
								true);
						$("#sea-login-btn").prop('disabled', true);
						login(username, password, seaLoginParams.country,
								seaLoginParams.errorMessage,
								'sea-mobile-ato-username', 'sea-mobile-ato-password',
								'sea-moile-login-btn');

					});

			$("#sea-mobile-logout-btn").click(function(e) {
				$("#sea-mobile-logout-btn").prop('disabled', true);
				logoutSEA();

			});

			var validateFields = function(username, password) {

				if (username.trim() != '' && password.trim() != '')
					return true;
				else
					return false;

			};

			var getAccessToken = function() {

				var token = ""
				var allcookies = document.cookie;
				var cookiearray = allcookies.split(';');

				for (var i = 0; i < cookiearray.length; i++) {
					name = cookiearray[i].split('=')[0];
					value = cookiearray[i].split('=')[1];
					console.log("name" + name + ">>>>" + value);
					if (name.trim() == 'dfx-auth-token') {

						token = value;
					}
				}

				return token;

			};



			var login = function(username, password, country, error, userfield,
					passwordfield, submitbutton) {

				if (!validateFields(username, password)) {
					alert('Please verify your Username and Password');
					$("input[name='" + userfield + "']")
							.prop('disabled', false);
					$("input[name='" + passwordfield + "']").prop('disabled',
							false);
					$("#" + submitbutton).prop('disabled', false);
				}

				else {

					$.ajax({
						url : '/bin/amwaytoday/login',
						type : "POST",
						data : {
							username : username,
							password : password,
							country : country
						},
						timeout : 30000,
						async : true,
						success : function(data) {

							if (data.message == 'success') {
								setCookie('dfx-auth-token',data.loginToken);
								setCookie('amway-today-login-type',data.loginType);
								location.reload();
							}

							else {
								alert(error);
								$("input[name='" + userfield + "']").prop(
										'disabled', false);
								$("input[name='" + passwordfield + "']").prop(
										'disabled', false);
								$("#" + submitbutton).prop('disabled', false);
							}

						},
						error : function(err, status, m) {

							alert(error);
							$("input[name='" + userfield + "']").prop(
									'disabled', false);
							$("input[name='" + passwordfield + "']").prop(
									'disabled', false);
							$("#" + submitbutton).prop('disabled', false);

						}

					});

				}

			};

            var deleteCookie = function(name) {
                $.cookie(name, "any_value", {path:'/', expires: -1 });
                console.log("deleted "+name);

			};

			var logoutSEA = function() {

				var tokenValue = getAccessToken();

				if (tokenValue != "") {
					var formData = {
						token : tokenValue
					};
					$.ajax({
						url : '/bin/amwaytoday/logout',
						dataType : "json",
						type : "POST",
						data : formData,
						timeout : 30000,
						async : false,
						success : function(data) {

							if (data.message == 'success') {
								//location.reload();
							}

							else {
								//deleteCookie('dfx-auth-token');
								//deleteCookie('amway-today-login-type');
								//location.reload();
							}

						},
						error : function(err, status, m) {

							//deleteCookie('dfx-auth-token');
							//deleteCookie('amway-today-login-type');
							//location.reload();

						}

					});

				} else {
					//deleteCookie('dfx-auth-token');
					//deleteCookie('amway-today-login-type');
					//location.reload();

				}

                	deleteCookie('dfx-auth-token');
					deleteCookie('amway-today-login-type');
					location.reload();

			};

		});
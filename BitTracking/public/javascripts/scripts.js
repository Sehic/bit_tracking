$("#button-back").click(function(){
    parent.history.back();
    return false;
});
//function will disable submit button if input field is whitespace
$(document).ready(function () {
    CheckInputs();
    $("#question").each(function () {
        $(this).keyup(function () {
            $("#addfaq").prop("disabled", CheckInputs());
        });
    });
});
function CheckInputs() {
    var valid = false;
    $("#question").each(function () {
        if (valid) {
            return valid;
        }
        var input = $.trim($(this).val());
        valid = !input;
    });
    return valid;
}

//function removes whitespaces from input field
function removeSpaces(string) {
    return string.split(' ').join('');
}

$(document).ready(function() {
    $(".drivingOfficeDiv").show();
    $("#userTypeId").change(function() {
        var userTypeValue = $("#userTypeId option:selected").val();
        if(userTypeValue === "2"){
            $(".drivingOfficeDiv").show();
        }else{
            $(".drivingOfficeDiv").hide();
        }
    });
});
//Forgot password functionality using ajax
$(document).ready(function() {
    var button = $('#sendPassword');
    var span = $('#sendingError');
    var loader = $('#loaderId');
    loader.hide();
    button.click(function() {
        var email = $('#forgotPasswordEmail').val();
        $.ajax({
            beforeSend: function() {
                loader.show();
                //span.html("Checking...");
            },
            url: '/forgotpassword',
            data: 'email=' + email,
            type: 'post'
        }).success(function(response) {
            span.html("Instructions sent to: \"" + email + "\" !").removeClass("alert-danger").addClass(alert-success);
        }).error(function(response) {
            span.html("Email \"" + email + "\" does not exist in our database!").removeClass("alert-success").addClass("alert-danger");
        })
    })
})
//Phone number validation using ajax
$(document).ready(function() {
    var submit = $('#validateSubmit');

    submit.click(function() {
        var enteredCode = $('#validatePhoneNumber').val();
        var span = $('#validationError');
        $.ajax({
            url: '/validate',
            data: "enteredCode=" + enteredCode,
            type: 'post'
        }).success(function(response) {
            var valDiv = $('#completeValidation');
            span.html("Successfully validated phone number " + response).attr("style", "font-size: 14px; color: green");
            $('#hideAfterValidation').hide();
        }).error(function() {
            span.html("Error! No such validation code found!").attr("style", "font-size: 14px; color: red; style: italic");
        })
    });
});
//Sending phone number code using ajax
$(document).ready(function() {
    var tryAgain = $('#tryAgainDiv').hide();
    $('#tryAgainId').click(function() {
        tryAgain.show();
    })

    var newCodeButton = $('#newCodeSubmit');
    var newSpan = $('#newSpan');
    newCodeButton.click(function() {
        var newNumber = $('#validatePhoneNumberAgain').val();
        var res = encodeURIComponent(newNumber);
        $.ajax({
            beforeSend: function() {
                newSpan.html("Please wait...");
            },
            url: '/validate/newCode',
            data: 'newNumber=' + res,
            type: 'post'
        }).success(function(response) {
            newSpan.html("New validation code sent to " + newNumber);
        }).error(function() {
            newSpan.html("Please enter correct phone number")
        })
    })
});


$(document).ready(function() {
    var phoneField = $('#phoneNumberFields').hide();
    var number = $('#inputCallingCode');
    var country = $('#countryId');

    country.change(function() {
        phoneField.slideDown();
        var value = $('#countryId option:selected').val();
        number.attr("value", "+" + value);
    })
});

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});

$(document).ready(function() {
    setInterval(function() {
        var number = '';
        $.ajax({
            method: 'get',
            url: '/indexajax'
        }).success(function(response) {
            var mybt = $('#mybt');
            number = response;
            mybt.html('My BT <span class=\"label label-warning\">' + number + '</span><strong class="caret"></strong>');
        })
    }, 500);
})

$(document).ready(function() {
    var showPackagesButton = $('#takePackagesId');
    var hidePackagesButton = $('#hideTakePackagesId');
    var tablePackages = $('#packagesToTake');
    var myPackages = $('#myPackages');
    var myPackagesDiv= $('#myPackagesDiv');
    var myPackages1 = $('#myPackages1');

    tablePackages.hide();
    hidePackagesButton.hide();
    myPackagesDiv.hide();
    myPackages1.hide();

    showPackagesButton.click(function() {
        tablePackages.slideDown();
        hidePackagesButton.show();
        showPackagesButton.hide();
    });

    hidePackagesButton.click(function() {
        tablePackages.slideUp();
        hidePackagesButton.hide();
        showPackagesButton.show();
    });
    myPackages.click(function() {
       myPackagesDiv.slideDown();
        myPackages.hide();
        myPackages1.show();
    });

    myPackages1.click(function() {
        myPackagesDiv.slideUp();
        myPackages.show();
        myPackages1.hide();
    });
});


$(document).ready(function(){
   $("#enableRouteButton").prop('disabled', true);
    $("#disableRouteButton").click( function(){
       $("#clickMe").prop('disabled', true);
       $('#addPackageButton').prop('disabled', false);
       $('#routeOffices').prop('disabled', true);
        $("#enableRouteButton").prop('disabled', false);
        $("#disableRouteButton").prop('disabled', true);
   })
    $("#enableRouteButton").click( function(){
        $("#clickMe").prop('disabled', false);
        $('#addPackageButton').prop('disabled', true);
        $('#routeOffices').prop('disabled', false);
        $("#disableRouteButton").prop('disabled', false);
        $("#enableRouteButton").prop('disabled', true);
    })

});

$(document).ready(function() {
    $('#mapUserLocation').hide();
    $('#recipientName').focusout(function() {
        $('#mapUserLocation').fadeIn();
    });
});

$(document).ready(function() {
    $('#rejectedPackages, #approvedPackages').click(function() {
        $(this).slideUp();
    })
});

$(document).ready(function() {
    $('#myPackageList').hide();
    $('#myPackages').click(function() {
        $('#waitingForApproval').show();
        $('#hideApproval').show();
        $('#approvalId').hide();
    });
});

$(document).ready(function() {
    $('#waitingForApproval').hide();
    $('#hideApproval').hide();
    $('#approvalId').click(function() {
        $('#waitingForApproval').show();
        $('#hideApproval').show();
        $('#approvalId').hide();
    });
});

$(document).ready(function() {
    $('#hideApproval').click(function() {
        $('#waitingForApproval').hide();
        $('#hideApproval').hide();
        $('#approvalId').show();
    });
});

$(document).ready(function() {
    $('#createPackage').hide();

    $('.myLinks').click(function() {
        var tablesToShow = $(this).attr('href');
        $(tablesToShow).show();
        $(tablesToShow).siblings('.usertables').hide();
        return false;
    });
});

//Sorting select boxes
$(document).ready(function(){
    sortDropDownListByText();
});

function sortDropDownListByText() {
// Loop for each select element on the page.
    $('.sortAlphabetically').each(function() {
// Keep track of the selected option.
        var selectedValue = $(this).val();
        $(this).html($('option', $(this)).sort(function(a, b) {
            return a.text.toUpperCase() == b.text.toUpperCase() ? 0 : a.text.toUpperCase() < b.text.toUpperCase() ? -1 : 1
        }));
// Select one option.
        $(this).val(selectedValue);
    });
}

var sortByText = function (a, b) {
    return $.trim($(a).text()) > $.trim($(b).text());
}
$(document).ready(function () {

    var sorted = $('#stuff label').sort(sortByText);
    $('#stuff').append(sorted);

});


//Calling table sort and search
$(document).ready(function () {
    $('.searchSortClass').DataTable();
});
//Method that append values to input field, and saving it to database
$(document).ready(function () {
    var valueOfSelect;
    var saveValueOfSelect = "";
    var alreadyOnRoute = "";
    var destinationOffice = $('#getDestinationOffice').val();
    var initialOffice = $('#getInitialOffice').val();
    var counter = 0;
    $('#finalizeButton').prop('disabled', true);

    // for any form on this page do the following
    $('#addToRoute').click(function () {

        //getting value from select box
        valueOfSelect = $('.selectOffice :selected').text();
        //saving values into string
        alreadyOnRoute += $('.selectOffice :selected').text() + ",";
        var alreadyOnRouteSplitted = alreadyOnRoute.split(",");

        $.ajax({
            url: "/adminpanel/makeroute/create",
            method: "POST",
            data: "name=" + valueOfSelect
        }).success(function (response) {
            var str = response;
            var splitted = str.split(",");
            //Removing elements from selectbox
            $('.selectOffice').empty();
            saveValueOfSelect += valueOfSelect + ",";
            //Adding new elements to selectbox
            $('#finalRoute').attr("value", saveValueOfSelect);
            //Comparing select box values and input field values
            //If we find same input, remove it from select box
            for (var j = 0; j < alreadyOnRouteSplitted.length - 1; j++) {

                for (var i = 0; i < splitted.length; i++) {
                    if (alreadyOnRouteSplitted[j] == splitted[i] || splitted[i] == initialOffice) {
                        var index = splitted.indexOf(splitted[i]);
                        if (index > -1) {
                            splitted.splice(index, 1);
                        }
                    }
                }
            }
            //Removing user click possibilities when he comes to final destination
            if (counter > 0) {
                $('.selectOffice').remove();
                $('#addToRoute').prop('disabled', true);
                $('#finalizeButton').prop('disabled', false);
                return;
            }
            //Searching for final destination
            for (var i = 0; i < splitted.length; i++) {
                $('.selectOffice').append("<option value=" + splitted[i] + ">" + splitted[i] + "</option>");
                if (splitted[i] == destinationOffice) {
                    counter++;
                }

            }
            //Case when we have only one office between start and end destination
            var splitFirstOffice = saveValueOfSelect.split(',');
            if (splitFirstOffice[0] == destinationOffice) {
                $('.selectOffice').remove();
                $('#addToRoute').prop('disabled', true);
                $('#finalizeButton').prop('disabled', false);
                return;
            }

        }).error(function (response) {
        });
    });
});

$(document).ready(function (){
    //Click button clear and reload page
    $('#clearFromRoute').click(function () {
        window.location.reload();
    });
});

//Method that shows package status for public user
$(document).ready(function () {

    $("#trackSubmit").click(function () {
        var number = $("#trackingNumber").val();
        $.ajax({
            url: "/checktrack",
            data: "trackingNumber=" + number,
            type: "POST"
        }).success(function (response) {
            var s = response;
            var splitted = s.split(",");
            $("#trackForm").show();
            $('#number').html(splitted[0]);
            $('#weight').html(splitted[1]);
            $('#price').html(splitted[2]);
            $('#shipFrom').html(splitted[3]);
            $('#destination').html(splitted[4]);
            if (splitted[5] == "null") {
                $('#time').html("");
            } else {
                $('#time').html(splitted[5]);
            }

            $('#status').html(splitted[6]);
        })
    })
});
//Method that is globally used when we delete something
$(document).ready(function () {
    $('body').on('click', 'a[data-role="delete"]', function (e) {
        e.preventDefault();
        $toDelete = $(this);
        var conf = bootbox.confirm("Are you sure that you want to delete this?", function (result) {
            if (result == true) {
                $.ajax({
                    url: $toDelete.attr("href"),
                    method: "delete"
                }).success(function (response) {
                    $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
                }).error(function(response){
                    bootbox.dialog({
                       message:"Post Office is active. You should remove relations with other offices and packages!",
                        title: "You can't delete this office!",
                        buttons: {
                            success: {
                                label: "Ok",
                                className: "btn btn-primary"
                            }
                        }
                    });
                });
            }
        });
    });
});
//Validating email using ajax
$(document).ready(function () {
    $("#inputEmail3").blur(function () {
        var email = $("#inputEmail3").val();
        var filter = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
        $.ajax({
            url: "register/check",
            type: "post",
            data: "email=" + email
        }).success(function (response) {
            if (!filter.test(email)) {
                $('#buttonsubmit').prop('disabled', true);
                $("#mailError").text("Invalid E-mail.");
            } else {
                $('#buttonsubmit').prop('disabled', false);
                $("#mailError").text("");
            }
        }).error(function (response) {
            $("#mailError").text("Email already exists");
            $('#buttonsubmit').prop('disabled', true);
        })
    });
});
//Checking post office name and address uniqueness
$(document).ready(function () {
    $('#address, #nameAddOffice').keyup(function () {
        var name = $("#nameAddOffice").val();
        var address = $("#address").val();
        $.ajax({
            url: "/adminpanel/tables/addpostoffice/checkname",
            type: "post",
            data: "name=" + name+"&address="+ address
        }).success(function (response) {
            $('#buttonsubmit').prop('disabled', false);
            $("#equalName").empty();
            $("#wrongAddress").empty();
        }).error(function (response) {
            var nesto = response.responseText;

            if(nesto === "1"){
                $("#equalName").text("Office with this name already exists!");
                $("#wrongAddress").text("Office with this address already exists!");
            }
            if(nesto === "2"){

                $("#wrongAddress").empty();
                $("#equalName").text("Office with this name already exists!");

            }
            if(nesto === "X"){
                $("#equalName").empty();
                $("#wrongAddress").text("Office with this address already exists!");

            }


            $('#buttonsubmit').prop('disabled', true);
        })
    });
});

//Cookie controller
function setCookies() {
    var email = document.getElementById("inputEmail").value;
    if (document.getElementById("box1").checked) {
        document.cookie = "email=" + email + "; expires=Thu, 18 Dec 2020 12:00:00 UTC";
    }
}
function deleteCookies() {
    document.cookie = "email=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
}

//Registration validation
function checkNumber() {
    var firstName = document.getElementById("inputPhoneNumber").value;
    var filter = /^[0-9]+$/;
    var phoneSpan = $('#phoneInfo');
    if (!filter.test(firstName) && firstName.length > 0) {
        phoneSpan.html("Only numbers allowed. No spaces between!").addClass("alert-danger").attr("style", "font-size: 14px");
    } else {
        phoneSpan.html("This field is not required. If you want to receive an SMS notifications about delivery status of your packages, please submit valid mobile number. If you do, you will get validation code to specified number.").removeClass("alert-danger").attr("style", "font-size: 10px");
    }
}

function checkFirstName() {
    var firstName = document.getElementById("inputName").value;
    var filter = /^[a-zA-Z]+$/;
    if (firstName.length == 0) {
        document.getElementById("nameError").innerHTML = "This field is required";
    } else {
        if (!filter.test(firstName)) {
            document.getElementById("nameError").innerHTML = "Only letters allowed.";
        } else {
            document.getElementById("nameError").innerHTML = "";
        }
    }
}

function checkLastName() {
    var lastName = document.getElementById("inputLastName").value;
    var filter = /^[a-zA-Z]+$/;
    if (lastName.length == 0) {
        document.getElementById("lastNameError").innerHTML = "This field is required";
    } else {
        if (!filter.test(lastName)) {
            document.getElementById("lastNameError").innerHTML = "Only letters allowed.";
        } else {
            document.getElementById("lastNameError").innerHTML = "";
        }
    }
}

function checkPassword() {
    var password = document.getElementById("inputPassword3").value;
    if (password.length == 0) {
        document.getElementById("passError").innerHTML = "This field is required";
    } else {
        if (password.length < 6) {
            document.getElementById("passError").innerHTML = "Password must have at least 6 characters.";
        } else if (password.search(/\d/) == -1) {
            document.getElementById("passError").innerHTML = "Password must have at least one number.";
        } else if (password.search(/[a-zA-Z]/)) {
            document.getElementById("passError").innerHTML = "Password must have at least one letter.";
        } else if (password.length > 6 && !password.search(/[a-zA-Z]/) && password.search(/\d/) != -1) {
            document.getElementById("passError").innerHTML = "";
        }
    }
}

function checkPasswords() {
    var password = document.getElementById("inputPassword3").value;
    var repassword = document.getElementById("inputPassword4").value;

    if (password != repassword) {
        document.getElementById("repassError").innerHTML = "Password must be same.";
    } else {
        document.getElementById("repassError").innerHTML = "";
    }
}


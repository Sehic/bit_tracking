$(document).ready(function() {
    var showPackagesButton = $('#takePackagesId');
    var hidePackagesButton = $('#hideTakePackagesId');
    var tablePackages = $('#packagesToTake');

    tablePackages.hide();
    hidePackagesButton.hide();

    showPackagesButton.click(function() {
        tablePackages.show();
        hidePackagesButton.show();
        showPackagesButton.hide();
    });

    hidePackagesButton.click(function() {
        tablePackages.hide();
        hidePackagesButton.hide();
        showPackagesButton.show();
    });
});


$(document).ready(function(){
   $("#disableRouteButton").click( function(){
       $("#clickMe").prop('disabled', true);
       $('#addPackageButton').prop('disabled', false);
       $('#routeOffices').prop('disabled', true);
   })
    $("#enableRouteButton").click( function(){
        $("#clickMe").prop('disabled', false);
        $('#addPackageButton').prop('disabled', true);
        $('#routeOffices').prop('disabled', false);
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
//Method that is globaly used when we delete something
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
             //   bootbox.alert("You can't delete this office!");
                });
            }
        });
    });
});

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


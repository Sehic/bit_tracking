$(document).ready(function(){
    sortDropDownListByText();
});

function sortDropDownListByText() {
// Loop for each select element on the page.
    $('#sortAlphabetically').each(function() {
// Keep track of the selected option.
        var selectedValue = $(this).val();
        $(this).html($('option', $(this)).sort(function(a, b) {
            return a.text.toUpperCase() == b.text.toUpperCase() ? 0 : a.text.toUpperCase() < b.text.toUpperCase() ? -1 : 1
        }));
// Select one option.
        $(this).val(selectedValue);
    });
}

//Calling table sort and search
$(document).ready(function () {
    $('#example').DataTable();
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
                });
            }
        });
    });
});

$(document).ready(function () {
    $("#inputEmail3").blur(function () {
        var email = $("#inputEmail3").val();
        $.ajax({
            url: "register/check",
            type: "post",
            data: "email=" + email
        }).success(function (response) {
            $('#buttonsubmit').prop('disabled', false);
        }).error(function (response) {
            $("#mailError").text("Email already exists");
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
function checkEmail() {
    var email = document.getElementById("inputEmail3").value;
    var filter = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    if (!filter.test(email)) {
        document.getElementById("mailError").innerHTML = "Invalid E-mail.";
    } else {
        document.getElementById("mailError").innerHTML = "";
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


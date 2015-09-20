$(document).ready(function(){
    var valueOfSelect;
    var saveValueOfSelect="";
    // for any form on this page do the follofing
    $('#addToRoute').click(function(){
        valueOfSelect = $('.selectOffice :selected').text();
        $.ajax({
            url: "/adminpanel/makeroute/create",
            method: "POST",
            data:"name="+valueOfSelect
        }).success(function(response){
            var str = response;
            console.log("Response = "+response);
            var splitted = str.split(",");
            $('.selectOffice').empty();
            saveValueOfSelect+=valueOfSelect+",";
            $('#finalRoute').attr("value", saveValueOfSelect);
            for(var i=0;i<splitted.length;i++){

                $('.selectOffice').append("<option value="+splitted[i]+">"+splitted[i]+"</option>");
            }

        }).error(function(response){
        });
    });


    $("#search").keyup(function() {
        var search = $("#search").val();
        var exp = new RegExp(search, "i");
        $.ajax({
            beforeSend: function() {
                $("#edin").html("Searching...")
            },
            url: '/adminpanel/package/json',
            type: 'POST',
            success: function(response) {
                var tbody = '<thead><tr><th>#</th><th>Tracking Number</th><th>Destination</th><th>Post Office</th></tr></thead><tbody>';
                $.each(response, function(key, val) {
                    if (val.office != null && (val.destination.search(exp) != -1 || val.office.search(exp) != -1)) {
                        tbody += '<tr class="succes"><td>' + val.id + '</td>'
                        tbody += '<td>' + val.trackingNum + '</td>';
                        tbody += '<td>' + val.destination + '</td>';
                        tbody += '<td>' + val.office + '</td>';
                        tbody += '</tr>';
                    }
                });
                tbody += '</tbody>';
                $("#edin").html(tbody);
            }
        });
    });


});


$('body').on('click', 'a[data-role="delete"]', function(e){
    e.preventDefault();
    $toDelete = $(this);
    var conf = bootbox.confirm("Delete?", function(result){
        if(result != null){
            $.ajax({
                url: $toDelete.attr("href"),
                method: "delete"
            }).success(function(response){
                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
            });
        }
    });


});




$(".dropdown-menu li a").click(function () {
    var selText = $(this).text();
    $(this).parents('.btn-group').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');
});

$("#btnSearch").click(function () {
    alert($('.btn-select').text() + ", " + $('.btn-select2').text());
});


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


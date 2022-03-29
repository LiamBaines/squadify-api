window.onload = function() {

    var input = document.getElementById("input");
    var submit = document.getElementById("submit");

    input.addEventListener("input", e => {
        if (input.value == null || input.value == "") {
            submit.setAttribute("disabled", "");
        } else {
            submit.removeAttribute("disabled");
        }
    });

}

document.getElementById("mobile-menu").addEventListener("click", function() {
    var sidebar = document.getElementById("sidebar");
    sidebar.classList.toggle("translate-x-full");
    sidebar.classList.toggle("right-0");
    sidebar.classList.toggle("left-0");
}, false);
console.log("✅ app.js cargado");

document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ DOMContentLoaded");

    document.body.addEventListener('click', function(e) {
        if (e.target.closest('#logoutLink')) {
            console.log("👉 click en logoutLink detectado");
            e.preventDefault();

            const logoutForm = document.querySelector('#logoutForm');
            console.log("👉 logoutForm:", logoutForm);

            if (logoutForm) logoutForm.submit();
        }
    });
});

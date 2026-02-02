
document.addEventListener('DOMContentLoaded', () => {

    const lnSalir = document.querySelector('#logoutLink');
    lnSalir.addEventListener('click', (e) => {
        e.preventDefault();
        document.querySelector('#logoutForm').submit();
    })
})
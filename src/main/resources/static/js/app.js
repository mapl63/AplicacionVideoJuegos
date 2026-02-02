document.addEventListener('DOMContentLoaded', () => {
    document.body.addEventListener('click', function(e) {
        if (e.target.closest('#logoutLink')) {
            e.preventDefault();
            const logoutForm = document.querySelector('#logoutForm');
            if (logoutForm) logoutForm.submit();
        }
    });
});

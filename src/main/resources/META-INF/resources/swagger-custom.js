window.addEventListener('load', function () {
  function aplicarIdentidade() {
    var topbar = document.querySelector('.swagger-ui .topbar');
    if (!topbar) return;

    // Já aplicado
    if (document.getElementById('caixa-header')) return;

    // Estilo do topbar
    topbar.style.cssText = 'background-color:#1c60ab !important; border-bottom:5px solid #ef9c00; padding:10px 20px; display:flex; align-items:center;';

    // Esconde conteúdo original
    var wrapper = topbar.querySelector('.topbar-wrapper');
    if (wrapper) {
      wrapper.innerHTML = '';

      // Logo SVG inline
      var logo = document.createElement('div');
      logo.id = 'caixa-header';
      logo.style.cssText = 'display:flex; align-items:center; gap:14px; width:100%;';
      logo.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 80 80" width="54" height="54">
          <rect width="80" height="80" rx="12" fill="#ffffff" fill-opacity="0.15"/>
          <polygon points="10,30 40,10 70,30" fill="#ef9c00"/>
          <rect x="12" y="30" width="56" height="30" fill="#ffffff"/>
          <rect x="18" y="33" width="8" height="24" rx="1" fill="#1c60ab"/>
          <rect x="30" y="33" width="8" height="24" rx="1" fill="#1c60ab"/>
          <rect x="42" y="33" width="8" height="24" rx="1" fill="#1c60ab"/>
          <rect x="54" y="33" width="8" height="24" rx="1" fill="#1c60ab"/>
          <rect x="8" y="60" width="64" height="7" rx="2" fill="#ef9c00"/>
          <text x="40" y="74" text-anchor="middle" font-family="Arial" font-weight="bold" font-size="8" fill="#ffffff">CAIXA</text>
        </svg>
        <div style="display:flex; flex-direction:column; gap:2px;">
          <span style="color:#ffffff; font-size:19px; font-weight:700; font-family:Arial,sans-serif; letter-spacing:0.3px;">CAIXA Econômica Federal</span>
          <span style="color:#ef9c00; font-size:13px; font-family:Arial,sans-serif; font-weight:600; letter-spacing:0.5px;">Simulador de Financiamentos — API v1.0.0</span>
        </div>
      `;
      wrapper.appendChild(logo);
    }
  }

  // Tenta aplicar imediatamente e depois com delay (Swagger carrega async)
  aplicarIdentidade();
  setTimeout(aplicarIdentidade, 500);
  setTimeout(aplicarIdentidade, 1500);
  setTimeout(aplicarIdentidade, 3000);
});

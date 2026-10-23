import Logo from './Logo.jsx';

export default function Navbar({ modulo, onAcortar, onVerEnlaces }) {
  return (
    <header className="navbar">
      <div className="navbar-interior">
        <a
          className="marca"
          href="/"
          aria-label="atajo, ir al inicio"
          onClick={(e) => {
            e.preventDefault();
            onAcortar();
          }}
        >
          <Logo />
          <span className="marca-nombre">atajo</span>
        </a>

        <nav className="nav" aria-label="Módulos">
          <button
            type="button"
            className="nav-boton"
            aria-current={modulo === 'acortar' ? 'page' : undefined}
            onClick={onAcortar}
          >
            Acortar enlace
          </button>
          <button
            type="button"
            className="nav-boton"
            aria-current={modulo === 'ver' ? 'page' : undefined}
            onClick={onVerEnlaces}
          >
            Ver enlaces
          </button>
        </nav>

        <div className="navbar-lado" aria-hidden="true" />
      </div>
    </header>
  );
}

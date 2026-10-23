/** Marca de "atajo": el camino largo (punteado) frente a la diagonal directa. */
export default function Logo({ tamano = 28 }) {
  return (
    <svg className="logo" width={tamano} height={tamano} viewBox="0 0 32 32" aria-hidden="true">
      <rect width="32" height="32" rx="8" className="logo-fondo" />
      <path d="M9 23V9h14" className="logo-largo" />
      <path d="M9 23L23 9M16 9h7v7" className="logo-corto" />
    </svg>
  );
}

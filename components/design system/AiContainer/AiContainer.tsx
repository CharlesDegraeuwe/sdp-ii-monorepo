const AiContainer = () => {
  return (
    <div
      className="w-full h-full rounded-3xl flex items-center justify-center bg-white overflow-hidden"
      style={{
        boxShadow: `
    inset 150px 150px 200px -120px #60a5fa80,
    inset -150px 150px 200px -120px #fbbf2480,
    inset 150px -150px 200px -120px #fb718580,
    inset -150px -150px 200px -120px #a855f780
`,
      }}
    ></div>
  );
};

AiContainer.displayName = 'AiContainer';
export default AiContainer;

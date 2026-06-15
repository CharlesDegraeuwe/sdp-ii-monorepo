import { PageSectionProps } from '@/components/design-system/PageSection/PageSection.types';
import { Container } from '@/components/design-system/Container';

const PageSection = (props: PageSectionProps) => {
  const { children, title, icon, danger } = props;
  return (
    <Container>
      <div className={'flex flex-row gap-3 items-center w-full'}>
        {icon}
        <span
          className={`font-bold text-lg first-letter:uppercase ${danger && 'text-rose-700'}`}
        >
          {title}:
        </span>
      </div>
      {children}
    </Container>
  );
};

PageSection.displayName = 'PageSection';
export default PageSection;

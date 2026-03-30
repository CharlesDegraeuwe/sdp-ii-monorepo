import Sidebar from "@/components/app/structuur/sidebar";
import AppHeader from "@/components/app/structuur/header";

export default function AppLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <>

            <AppHeader/>
            <section className={"flex flex-row w-full h-full min-h-full bg-bg-white"}>
                <Sidebar/>
                <main>{children}</main>;
            </section>
        </>
    );
}

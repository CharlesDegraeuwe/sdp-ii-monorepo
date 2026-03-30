import { redirect } from "next/navigation";
import { auth } from "@/auth";

export default async function Rootpage() {
    const session = await auth();

    if (session) {
        redirect(`/overzicht`);
    } else {
        redirect(`/login`);
    }
}
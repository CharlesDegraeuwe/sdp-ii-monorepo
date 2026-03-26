"use client"
import Link from "next/link";
import { IoMdNotificationsOutline } from "react-icons/io";
import { FaUser, FaAngleDown } from "react-icons/fa6";
import {useEffect, useRef, useState} from "react";
import { IoExitOutline } from "react-icons/io5";

export default function AppHeader() {
    const [isOpen, setIsOpen] = useState(false);
    const triggerRef = useRef<HTMLDivElement>(null);
    const popupRef = useRef<HTMLDivElement>(null);
    const handleToggle = () => {
        setIsOpen(prevState => !prevState);
    }

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (
                popupRef.current &&
                !popupRef.current.contains(e.target as Node) &&
                triggerRef.current &&
                !triggerRef.current.contains(e.target as Node)
            ) {
                setIsOpen(false);
            }
        };

        if (isOpen) {
            setTimeout(() => {
                document.addEventListener("mousedown", handleClickOutside);
            }, 0);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [isOpen, setIsOpen, triggerRef]);


    return (<div className={'absolute top-0 w-screen h-fit pt-10 flex flex-row justify-end px-14 gap-5'}>
        <Link href={"/notificaties"} className={"w-10 h-10 active:scale-95 transition-all duration-300 flex items-center justify-center rounded-full bg-bg-white border drop-shadow-xl border-zinc-300/30"}>
            <IoMdNotificationsOutline size={25} />
        </Link>
        <Link href={"/account"} className={"w-10 h-10 active:scale-95 transition-all duration-300 flex items-center justify-center rounded-full bg-bg-white border drop-shadow-xl border-zinc-300/30"}>
            <FaUser size={17} />
        </Link>
        <div className={"relative"}>
            <div onClick={handleToggle} ref={triggerRef} className={"relative w-35 h-10 bg-bg-white border cursor-pointer select-none drop-shadow-2xl active:scale-95 transition-all duration-300 border-zinc-300/30 rounded-full flex items-center justify-center px-5"}>
                <span className={"w-full h-full flex items-center pointer-events-none font-bold"}>gebruiker</span>
                <FaAngleDown className={`${isOpen ? "rotate-180" : "rotate-0"} transition-all duration-300`} size={20} />
            </div>
            <div ref={popupRef}
                 className={`absolute top-full left-1/2 -translate-x-1/2 mt-4
                z-[9999] w-35 p-2
                rounded-2xl text-black backdrop-blur-2xl
                bg-white 
                border border-zinc-300/30
                shadow-xl shadow-black/10 dark:shadow-black/30
                transition-all duration-300 ease-out origin-top
                ${isOpen
                             ? "opacity-100 scale-100 translate-y-0 visible pointer-events-auto"
                             : "opacity-0 scale-95 -translate-y-2 invisible pointer-events-none"}
              `}
                         onClick={(e) => e.stopPropagation()}
            >
                <button className={"w-full hover:bg-zinc-400/20 rounded-lg p-2 flex flex-row items-center justify-between cursor-pointer"}>
                    <span>Log out</span>
                    <IoExitOutline />
                </button>
            </div>
        </div>
    </div>);
}
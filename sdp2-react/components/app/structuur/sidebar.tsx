'use client'
import { FiSidebar } from "react-icons/fi";
import { useEffect, useRef, useState, useCallback } from "react";
import Link from "next/link";

export default function Sidebar() {
    const [collapsed, setCollapsed] = useState(true);
    const [isDragging, setIsDragging] = useState(false);
    const startXRef = useRef(0);
    const wasCollapsedRef = useRef(true);

    const handleDragStart = (e: React.MouseEvent) => {
        e.preventDefault();
        setIsDragging(true);
        startXRef.current = e.clientX;
        wasCollapsedRef.current = collapsed;
        document.body.style.cursor = 'ew-resize';
        document.body.style.userSelect = 'none';
    };

    const handleDragMove = useCallback((e: MouseEvent) => {
        if (!isDragging) return;
        const deltaX = e.clientX - startXRef.current;

        if (wasCollapsedRef.current && deltaX > 30) {
            setCollapsed(false);
        } else if (!wasCollapsedRef.current && deltaX < -30) {
            setCollapsed(true);
        }
    }, [isDragging]);

    const handleDragEnd = useCallback(() => {
        if (!isDragging) return;
        setIsDragging(false);
        document.body.style.cursor = '';
        document.body.style.userSelect = '';
    }, [isDragging]);

    useEffect(() => {
        if (!isDragging) return;
        window.addEventListener('mousemove', handleDragMove);
        window.addEventListener('mouseup', handleDragEnd);
        return () => {
            window.removeEventListener('mousemove', handleDragMove);
            window.removeEventListener('mouseup', handleDragEnd);
        };
    }, [isDragging, handleDragMove, handleDragEnd]);

    return (
        <div className={`${collapsed ? "w-30" : "w-50"} h-screen flex flex-col z-[998] transition-all duration-200 bg-delaware_red rounded-tr-3xl rounded-br-3xl py-10`}>
            <div className="flex flex-col items-center justify-baseline h-full text-white font-sfpro px-5 gap-20">
                <button onClick={() => setCollapsed(!collapsed)} className="p-2 hover:opacity-70 cursor-pointer transition-opacity">
                    <FiSidebar size={23} />
                </button>

                <div className={"w-full h-full flex flex-col gap-10"}>
                    <Link href="/home" className="w-full h-1/10 flex flex-col gap-1 justify-center items-center text-sm hover:opacity-70 transition-opacity rounded-3xl bg-delaware_dark_red">
                        <span className={`${collapsed ? "hidden" : "block"} truncate`}>dashboard</span>
                    </Link>

                </div>

            </div>

            <div
                onMouseDown={handleDragStart}
                className="absolute right-0 top-0 h-full w-2 cursor-ew-resize"
            />
        </div>
    );
}
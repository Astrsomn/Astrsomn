import type {Component} from 'vue';

export type AdminModuleNavItem = {
    to: string;
    label: string;
    icon?: Component;
};

export type AdminModuleNavGroup = {
    key: string;
    label: string;
    icon?: Component;
    children: AdminModuleNavItem[];
};

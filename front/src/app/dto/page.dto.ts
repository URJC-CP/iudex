export interface PageDTO<T> {
    content: Array<T>;
    empty: boolean;
    first: boolean;
    last: boolean;
    number: number;
    numberOfElements: number;
    pageable: any;
    size: number;
    sort: any;
    totalElements: number; 
    totalPages: number;
}
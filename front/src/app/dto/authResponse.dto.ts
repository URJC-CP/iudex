import { TokenDTO } from "./token.dto";

export interface AuthResponseDTO {
    status?: string;
    message?: string;
    error?: string;
    accessToken?: TokenDTO;
    refreshToken?: TokenDTO;
  }
export interface AuthResponseDTO {
  status?: string;
  message?: string;
  error?: string;
  accessToken: string;
  refreshToken: string;
}